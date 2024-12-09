package am.ik.blog.notification;

import am.ik.blog.CommentApiProps;
import am.ik.blog.comment.Comment;
import am.ik.blog.comment.CommentCreatedEvent;
import am.ik.blog.comment.CommentStatusChangedEvent;
import org.springframework.http.MediaType;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GoogleChatWebhookEventListener {

	private final RestClient restClient;

	public GoogleChatWebhookEventListener(RestClient.Builder restClientBuilder, CommentApiProps props) {
		this.restClient = restClientBuilder.baseUrl(props.googleChatWebhookUrl()).build();
	}

	@ApplicationModuleListener
	public void onCommentCreated(CommentCreatedEvent event) {
		Comment comment = event.comment();
		this.restClient.post()
			.contentType(MediaType.APPLICATION_JSON)
			.body(Map.of("text",
					"onCommentCreated: entryId=%s commentId=%s".formatted(comment.getEntryId(),
							comment.getCommentId())))
			.retrieve()
			.toBodilessEntity();
	}

	@ApplicationModuleListener
	public void onCommentStatusChanged(CommentStatusChangedEvent event) {
		Comment comment = event.comment();
		this.restClient.post()
			.contentType(MediaType.APPLICATION_JSON)
			.body(Map.of("text",
					"onCommentStatusChanged: entryId=%s commentId=%s previousStatus=%s newStatus=%s".formatted(
							comment.getEntryId(), comment.getCommentId(), event.previousStatus(), comment.getStatus())))
			.retrieve()
			.toBodilessEntity();
	}

}
