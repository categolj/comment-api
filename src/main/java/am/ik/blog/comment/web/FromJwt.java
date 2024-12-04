package am.ik.blog.comment.web;

import am.ik.blog.comment.Commenter;
import am.ik.blog.comment.CommenterBuilder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;

public final class FromJwt {

	public static Commenter toCommenter(Jwt jwt) {
		String id = jwt.getSubject();
		String name = Objects.toString(jwt.getClaimAsString("name"), "N/A");
		String email = Objects.toString(jwt.getClaimAsString("email"), "N/A");
		String picture = Objects.toString(jwt.getClaimAsString("picture"), "https://placehold.jp/32x32.png");
		return CommenterBuilder.commenter().id(id).name(name).email(email).picture(picture).build();
	}

}
