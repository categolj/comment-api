package am.ik.blog.comment;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface CommentRepository extends ListCrudRepository<Comment, Long> {

	List<Comment> findByEntryIdOrderByCreatedAtAsc(Long entryId);

}
