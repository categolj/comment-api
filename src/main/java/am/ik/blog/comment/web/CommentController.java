package am.ik.blog.comment.web;

import am.ik.blog.comment.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CommentController {

	private final CommentService commentService;

	private final CommentRepository commentRepository;

	public CommentController(CommentService commentService, CommentRepository commentRepository) {
		this.commentService = commentService;
		this.commentRepository = commentRepository;
	}

	@PostMapping(path = "/entries/{entryId}/comments")
	public Comment createComment(@PathVariable Long entryId, @RequestBody CommentRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		Commenter commenter = FromJwt.toCommenter(jwt);
		return this.commentService.create(entryId, request.body(), commenter);
	}

	@GetMapping(path = "/entries/{entryId}/comments")
	public List<Comment> listComments(@PathVariable Long entryId) {
		return this.commentRepository.findByEntryIdOrderByCreatedAtAsc(entryId)
			.stream()
			.filter(Comment::isApproved)
			.peek(comment -> {
				Commenter commenter = comment.getCommenter();
				comment.setCommenter(CommenterBuilder.commenter()
					.id(commenter.getId())
					.name(commenter.getName())
					.picture(commenter.getPicture())
					.email("")
					// mask email
					.build());
			})
			.toList();
	}

	@PatchMapping(path = "/comments/{commentId}")
	public Optional<Comment> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequest request) {
		return this.commentService.changeStatus(commentId, request.status());
	}

	@DeleteMapping(path = "/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Transactional
	public void deleteComment(@PathVariable Long commentId) {
		this.commentService.findOwnComment(commentId).ifPresent(this.commentRepository::delete);
	}

	public record CommentRequest(String body) {
	}

	public record CommentUpdateRequest(Comment.Status status) {
	}

}
