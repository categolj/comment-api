package am.ik.blog.security;

import am.ik.blog.CommentApiProps;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthorizationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	private final CommentApiProps props;

	public JwtAuthorizationConverter(CommentApiProps props) {
		this.props = props;
	}

	@Override
	public Collection<GrantedAuthority> convert(Jwt source) {
		String email = source.getClaimAsString("email");
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (this.props.admins().contains(email)) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return authorities;
	}

}
