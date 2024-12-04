package am.ik.blog.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.jilt.Builder;

@Entity
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Commenter {

	@Id
	@Column(nullable = false)
	private String id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column
	private String picture;

	public Commenter() {
	}

	public Commenter(String id, String name, String email, String picture) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	@Override
	public String toString() {
		return "Commenter{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", email='" + email + '\'' + ", picture='"
				+ picture + '\'' + '}';
	}

}
