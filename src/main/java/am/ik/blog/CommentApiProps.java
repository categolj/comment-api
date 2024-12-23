package am.ik.blog;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.Set;

@ConfigurationProperties(prefix = "comment")
public record CommentApiProps(Set<String> admins, URI googleChatWebhookUrl) {
}
