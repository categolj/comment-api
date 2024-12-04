package am.ik.blog.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.jilt.Builder;

import java.time.OffsetDateTime;

@Entity
@Builder
@DynamicUpdate
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	@Column(nullable = false)
	private Long entryId;

	@Column(nullable = false)
	private String body;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "commenter_id", referencedColumnName = "id", nullable = false)
	private Commenter commenter;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(nullable = false)
	private OffsetDateTime createdAt;

	public enum Status {

		PENDING, APPROVED, REJECTED;

	}

	public Comment() {
	}

	public Comment(Long commentId, Long entryId, String body, Commenter commenter, Status status,
			OffsetDateTime createdAt) {
		this.commentId = commentId;
		this.entryId = entryId;
		this.body = body;
		this.commenter = commenter;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getEntryId() {
		return entryId;
	}

	public void setEntryId(Long entryId) {
		this.entryId = entryId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Commenter getCommenter() {
		return commenter;
	}

	public void setCommenter(Commenter commenter) {
		this.commenter = commenter;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Comment{" + "commentId=" + commentId + ", entryId=" + entryId + ", body='" + body + '\''
				+ ", commenter=" + commenter + ", status=" + status + ", createdAt=" + createdAt + '}';
	}

	@JsonIgnore
	public boolean isApproved() {
		return status == Status.APPROVED;
	}

}
