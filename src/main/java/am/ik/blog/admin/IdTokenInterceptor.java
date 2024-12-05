package am.ik.blog.admin;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.IOException;

public class IdTokenInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof OAuth2AuthenticationToken token
				&& token.getPrincipal() instanceof OidcUser oidcUser) {
			request.getHeaders().setBearerAuth(oidcUser.getIdToken().getTokenValue());
		}
		return execution.execute(request, body);
	}

}
