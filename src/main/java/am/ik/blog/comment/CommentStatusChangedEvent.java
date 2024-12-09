package am.ik.blog.comment;

public record CommentStatusChangedEvent(Comment comment, Comment.Status previousStatus) {

}
