package am.ik.blog.admin.web;

import am.ik.blog.admin.IdTokenInterceptor;
import am.ik.blog.comment.Comment;
import am.ik.blog.comment.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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

	@GetMapping(path = "/admin/whoami", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object hello() {
		return this.restClient.get().uri("/whoami").retrieve().body(byte[].class);
	}

	@GetMapping(path = "/admin/login", params = "redirect_path", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> login(@RequestParam(name = "redirect_path") String redirectPath,
			@RequestHeader(name = HttpHeaders.REFERER, required = false) URI referer,
			UriComponentsBuilder uriComponentsBuilder) {
		if (!redirectPath.startsWith("/")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'redirectPath' must start with '/'");
		}
		UriComponents uriComponents = uriComponentsBuilder.path(redirectPath).build();
		String location;
		if (referer.getPort() == 5174) {
			// behind the vite proxy in the dev-mode
			location = UriComponentsBuilder.fromUriString(uriComponents.toUriString())
				.port(referer.getPort())
				.toUriString();
		}
		else {
			location = uriComponents.toUriString();
		}
		return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, location).build();
	}

	@GetMapping(path = "/admin/comments", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object comments() {
		return this.restClient.get().uri("/comments").retrieve().body(byte[].class);
	}

	@PatchMapping(path = "/admin/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
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
