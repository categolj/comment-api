package am.ik.blog.comment;

import am.ik.blog.TestcontainersConfiguration;
import jakarta.persistence.EntityManager;
import net.ttddyy.observation.boot.autoconfigure.DataSourceObservationAutoConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "logging.level.org.springframework.orm.jpa.JpaTransactionManager=debug")
@Testcontainers(disabledWithoutDocker = true)
@Import({ TestcontainersConfiguration.class })
class CommentRepositoryTest {

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	CommenterRepository commenterRepository;

	@Autowired
	JdbcClient jdbcClient;

	@Autowired
	TransactionTemplate tx;

	@Test
	void shouldSuccessfullySaveCommentIfCommenterDoesNotExist() {
		String commenterId = UUID.randomUUID().toString().replace("-", "");
		Commenter commenter = this.commenterRepository.findById(commenterId)
			.orElseGet(() -> CommenterBuilder.commenter()
				.id(commenterId)
				.email("john@example.com")
				.name("John Doe")
				.picture("https://example.com/32x32.jpg")
				.build());
		Comment comment = CommentBuilder.comment()
			.entryId(100L)
			.body("Hello World!")
			.commenter(commenter)
			.createdAt(OffsetDateTime.now())
			.status(Comment.Status.APPROVED)
			.build();
		Comment saved = this.commentRepository.save(comment);
		assertThat(saved.getCommentId()).isGreaterThan(0);
	}

	@Test
	void shouldSuccessfullySaveCommentIfCommenterExists() {
		// Create a commenter in advance
		String commenterId = UUID.randomUUID().toString().replace("-", "");
		Integer inserted = tx.execute(status -> this.jdbcClient.sql("""
				INSERT INTO commenter(id, email, name, picture) VALUES (:id, :email, :name, :picture)
				""")
			.paramSource(new BeanPropertySqlParameterSource(CommenterBuilder.commenter()
				.id(commenterId)
				.email("john@example.com")
				.name("John Doe")
				.picture("https://example.com/32x32.jpg")
				.build()))
			.update());
		assertThat(inserted).isEqualTo(1);

		Commenter commenter = this.commenterRepository.findById(commenterId)
			.orElseGet(() -> CommenterBuilder.commenter()
				.id(commenterId)
				.email("john@example.com")
				.name("John Doe")
				.picture("https://example.com/32x32.jpg")
				.build());
		Comment comment = CommentBuilder.comment()
			.entryId(100L)
			.body("Hello World!")
			.commenter(commenter)
			.createdAt(OffsetDateTime.now())
			.status(Comment.Status.APPROVED)
			.build();
		Comment saved = this.commentRepository.save(comment);
		assertThat(saved.getCommentId()).isGreaterThan(0);
	}

}