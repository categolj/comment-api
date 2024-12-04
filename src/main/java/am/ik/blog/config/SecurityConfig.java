package am.ik.blog.config;

import am.ik.blog.CommentApiProps;
import am.ik.blog.security.JwtAuthorizationConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.JwkSetUriJwtDecoderBuilderCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.authorizeHttpRequests(authorize -> authorize.requestMatchers(EndpointRequest.toAnyEndpoint())
				.permitAll()
				.requestMatchers(HttpMethod.GET, "/entries/{entryId}/comments")
				.permitAll()
				.anyRequest()
				.authenticated())
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
	};

}