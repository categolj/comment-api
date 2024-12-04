package am.ik.blog.comment;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CommenterRepository extends Repository<Commenter, String> {

	Optional<Commenter> findById(String id);

}
