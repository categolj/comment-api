package am.ik.blog.comment.web;

import am.ik.blog.MockConfig;
import am.ik.blog.OAuth2;
import am.ik.blog.TestcontainersConfiguration;
import am.ik.blog.comment.Comment;
import am.ik.blog.comment.CommentBuilder;
import am.ik.blog.comment.CommentRepository;
import am.ik.blog.comment.CommenterBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = { "logging.level.org.springframework.web.client.RestTemplate=info",
				"spring.http.client.redirects=dont_follow", "comment.admins=test@example.com",
				"comment.google-chat-webhook-url=https://httpbin.org/post" })
@Import({ TestcontainersConfiguration.class, MockConfig.class })
@Testcontainers(disabledWithoutDocker = true)
class CommentControllerIntegrationTest {

	RestClient restClient;

	String accessToken;

	@BeforeEach
	void setUp(@LocalServerPort int port, @Autowired RestClient.Builder builder,
			@Autowired LogbookClientHttpRequestInterceptor logbookClientHttpRequestInterceptor,
			@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUrl) {
		RestClient rest = builder.requestInterceptor(logbookClientHttpRequestInterceptor)
			.defaultStatusHandler(s -> true, (request, response) -> {
				/* no-op */})
			.build();
		this.accessToken = OAuth2.authorizationCodeFlow(URI.create(issuerUrl), rest,
				new OAuth2.User("test@example.com", "test"), new OAuth2.Client("google", "google"),
				URI.create("http://localhost:8080/login/oauth2/code/google"), Set.of("openid", "email", "profile"));
		this.restClient = builder.baseUrl("http://localhost:" + port).build();
	}

	@Test
	void shouldWorkInRegularScenario() {
		Long entryId = 100L;
		ResponseEntity<Comment> comment1 = this.restClient.post()
			.uri("/entries/{entryId}/comments", entryId)
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"body": "Hello World 1"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(comment1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(comment1.getBody()).isNotNull();
		assertThat(comment1.getBody().getStatus()).isEqualTo(Comment.Status.PENDING);
		ResponseEntity<Comment> comment2 = this.restClient.post()
			.uri("/entries/{entryId}/comments", entryId)
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"body": "Hello World 2"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(comment2.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(comment2.getBody()).isNotNull();
		assertThat(comment2.getBody().getStatus()).isEqualTo(Comment.Status.PENDING);
		ResponseEntity<Comment> comment3 = this.restClient.post()
			.uri("/entries/{entryId}/comments", entryId)
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"body": "Hello World 3"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(comment3.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(comment3.getBody()).isNotNull();
		assertThat(comment3.getBody().getStatus()).isEqualTo(Comment.Status.PENDING);
		ResponseEntity<Comment> comment4 = this.restClient.post()
			.uri("/entries/{entryId}/comments", entryId)
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"body": "Hello World 4"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(comment4.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(comment4.getBody()).isNotNull();
		assertThat(comment4.getBody().getStatus()).isEqualTo(Comment.Status.PENDING);
		ResponseEntity<Comment> rejected1 = this.restClient.patch()
			.uri("/comments/{commentId}", comment1.getBody().getCommentId())
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"status": "REJECTED"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(rejected1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(rejected1.getBody()).isNotNull();
		assertThat(rejected1.getBody().getStatus()).isEqualTo(Comment.Status.REJECTED);
		ResponseEntity<Comment> approved2 = this.restClient.patch()
			.uri("/comments/{commentId}", comment2.getBody().getCommentId())
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"status": "APPROVED"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(approved2.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(approved2.getBody()).isNotNull();
		assertThat(approved2.getBody().getStatus()).isEqualTo(Comment.Status.APPROVED);
		ResponseEntity<Comment> approved4 = this.restClient.patch()
			.uri("/comments/{commentId}", comment4.getBody().getCommentId())
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{"status": "APPROVED"}
					""")
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toEntity(Comment.class);
		assertThat(approved4.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(approved4.getBody()).isNotNull();
		assertThat(approved4.getBody().getStatus()).isEqualTo(Comment.Status.APPROVED);
		{
			ResponseEntity<String> comments = this.restClient.get()
				.uri("/entries/{entryId}/comments", entryId)
				.retrieve()
				.toEntity(String.class);
			assertThat(comments.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(comments.getBody()).isNotNull();
			assertThat(comments.getBody()).isEqualToIgnoringNewLines(
					"""
							[{"commentId":2,"entryId":100,"body":"Hello World 2","commenter":{"id":"test@example.com","name":"N/A","picture":"https://placehold.jp/32x32.png"},"status":"APPROVED","createdAt":"2024-11-01T00:00:01Z"},{"commentId":4,"entryId":100,"body":"Hello World 4","commenter":{"id":"test@example.com","name":"N/A","picture":"https://placehold.jp/32x32.png"},"status":"APPROVED","createdAt":"2024-11-01T00:00:03Z"}]
							""");
		}
		ResponseEntity<Void> deleted2 = this.restClient.delete()
			.uri("/comments/{commentId}", comment2.getBody().getCommentId())
			.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
			.retrieve()
			.toBodilessEntity();
		assertThat(deleted2.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		{
			ResponseEntity<String> comments = this.restClient.get()
				.uri("/entries/{entryId}/comments", entryId)
				.retrieve()
				.toEntity(String.class);
			assertThat(comments.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(comments.getBody()).isNotNull();
			assertThat(comments.getBody()).isEqualToIgnoringNewLines(
					"""
							[{"commentId":4,"entryId":100,"body":"Hello World 4","commenter":{"id":"test@example.com","name":"N/A","picture":"https://placehold.jp/32x32.png"},"status":"APPROVED","createdAt":"2024-11-01T00:00:03Z"}]
							""");
		}
	}

	@Test
	void notAllowedToDeleteComment(@Autowired CommentRepository commentRepository) {
		Comment othersComment = CommentBuilder.comment()
			.body("test")
			.createdAt(OffsetDateTime.now())
			.status(Comment.Status.APPROVED)
			.entryId(100L)
			.commenter(CommenterBuilder.commenter()
				.id("other")
				.email("other@example.com")
				.picture("https://placehold.jp/32x32.png")
				.name("Jane Doh")
				.build())
			.build();
		Comment saved = commentRepository.save(othersComment);
		try {
			ResponseEntity<Void> delete = this.restClient.delete()
				.uri("/comments/{commentId}", saved.getCommentId())
				.headers(httpHeaders -> httpHeaders.setBearerAuth(this.accessToken))
				.retrieve()
				.toBodilessEntity();
			assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		finally {
			commentRepository.delete(saved);
		}
	}

}