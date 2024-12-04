package am.ik.blog.comment.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoamiController {

	@GetMapping(path = "/whoami")
	public Object whoami(@AuthenticationPrincipal Jwt jwt) {
		return FromJwt.toCommenter(jwt);
	}

}
