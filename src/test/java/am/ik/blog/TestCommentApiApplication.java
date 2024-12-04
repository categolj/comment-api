package am.ik.blog;

import org.springframework.boot.SpringApplication;

public class TestCommentApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(CommentApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
