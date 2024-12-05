package am.ik.blog.config;

import am.ik.blog.CommentApiProps;
import am.ik.blog.security.JwtAuthorizationConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.JwkSetUriJwtDecoderBuilderCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	@Order(1)
	public SecurityFilterChain admiSecurityFilterChain(HttpSecurity http) throws Exception {
		return http.securityMatcher("/admin/**", "/oauth2/**", "/login/**", "/logout")
			.authorizeHttpRequests(authorize -> authorize
			// @formatter:off
					.requestMatchers("/admin/login", "/admin/whoami").authenticated()
					.requestMatchers("/admin/**").hasRole("ADMIN"))
			// @formatter:on
			.oauth2Login(Customizer.withDefaults())
			.csrf(Customizer.withDefaults())
			.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests(authorize -> authorize
		// @formatter:off
				.requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
				.requestMatchers("/","/assets/*", "/*.html","/*.css","/*.js", "/*.ico", "/*.png", "/error", "/livez", "/readyz").permitAll()
				.requestMatchers(HttpMethod.GET, "/entries/{entryId}/comments").permitAll()
				.anyRequest().authenticated()
		// @formatter:on
		)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
			.csrf(csrf -> csrf.ignoringRequestMatchers("/entries/**", "/comments/**"))
			.build();
	}

	@Bean
	public JwkSetUriJwtDecoderBuilderCustomizer jwkSetUriJwtDecoderBuilderCustomizer(
			RestTemplateBuilder restTemplateBuilder) {
		return builder -> builder.restOperations(restTemplateBuilder.build());
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter(CommentApiProps props) {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new JwtAuthorizationConverter(props));
		return jwtAuthenticationConverter;
	}

	@Bean
	public OidcUserService oidcUserService(CommentApiProps props) {
		OidcUserService oidcUserService = new OidcUserService();
		oidcUserService.setOidcUserMapper((oidcUserRequest, oidcUserInfo) -> {
			Set<GrantedAuthority> authorities = new LinkedHashSet<>();
			OAuth2AccessToken token = oidcUserRequest.getAccessToken();
			for (String scope : token.getScopes()) {
				authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
			}
			String email = oidcUserRequest.getIdToken().getClaimAsString("email");
			if (props.admins().contains(email)) {
				authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			}
			return new DefaultOidcUser(authorities, oidcUserRequest.getIdToken(), oidcUserInfo);
		});
		return oidcUserService;
	}

}