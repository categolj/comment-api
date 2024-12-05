package am.ik.blog.comment;

import am.ik.blog.CommentApiProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.OffsetDateTime;

import java.util.Optional;

@Service
public class CommentService {

	private final CommentRepository commentRepository;

	private final CommenterRepository commenterRepository;

	private final CommentApiProps props;

	private final Clock clock;

	private final Logger log = LoggerFactory.getLogger(CommentService.class);

	public CommentService(CommentRepository commentRepository, CommenterRepository commenterRepository,
			CommentApiProps props, Clock clock) {
		this.commentRepository = commentRepository;
		this.commenterRepository = commenterRepository;
		this.props = props;
		this.clock = clock;
	}

	@Transactional
	public Comment create(Long entryId, String body, Commenter commenter) {
		Commenter merged = this.commenterRepository.findById(commenter.getId()).orElse(commenter);
		Comment comment = CommentBuilder.comment()
			.entryId(entryId)
			.body(body)
			.commenter(merged)
			.status(Comment.Status.PENDING)
			.createdAt(OffsetDateTime.now(this.clock))
			.build();
		Comment commented = this.commentRepository.save(comment);
		log.info("Created comment {}", commented);
		return commented;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public Iterable<Comment> listAll() {
		return this.commentRepository.findAll(Sort.by(Sort.Order.asc("createdAt")));
	}

	@PostAuthorize("returnObject.orElse(null)?.commenter.id == authentication.name")
	public Optional<Comment> findOwnComment(Long commentId) {
		return this.commentRepository.findById(commentId);
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public Optional<Comment> changeStatus(Long commentId, Comment.Status status) {
		return this.commentRepository.findById(commentId).map(comment -> {
			comment.setStatus(status);
			return this.commentRepository.save(comment);
		});
	}

}
