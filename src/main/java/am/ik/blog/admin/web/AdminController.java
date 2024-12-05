package am.ik.blog.admin.web;

import am.ik.blog.admin.IdTokenInterceptor;
import am.ik.blog.comment.Comment;
import am.ik.blog.comment.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
public class AdminController {

	private final RestClient restClient;

	private final CommentRepository commentRepository;

	public AdminController(RestClient.Builder restClientBuilder, @Value("${server.port}") int port,
			CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
		this.restClient = restClientBuilder.baseUrl("http://127.0.0.1:" + port)
			.requestInterceptor(new IdTokenInterceptor())
			.build();
	}

	@GetMapping(path = "/admin/whoami")
	public Object hello() {
		return this.restClient.get().uri("/whoami").retrieve().body(byte[].class);
	}

	@GetMapping(path = "/admin/comments")
	public Object comments() {
		return this.restClient.get().uri("/comments").retrieve().body(byte[].class);
	}

	@PatchMapping(path = "/admin/comments/{commentId}")
	public Object updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequest request) {
		return this.restClient.patch()
			.uri("/comments/{commentId}", commentId)
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.retrieve()
			.body(byte[].class);
	}

	@DeleteMapping(path = "/admin/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(@PathVariable Long commentId) {
		// exceptional operation!
		this.commentRepository.deleteById(commentId);
	}

	public record CommentUpdateRequest(Comment.Status status) {
	}

}
