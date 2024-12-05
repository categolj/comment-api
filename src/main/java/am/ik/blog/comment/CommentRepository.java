package am.ik.blog.comment;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CommentRepository
		extends ListCrudRepository<Comment, Long>, PagingAndSortingRepository<Comment, Long> {

	List<Comment> findByEntryIdOrderByCreatedAtAsc(Long entryId);

}
