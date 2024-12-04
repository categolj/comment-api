package am.ik.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CommentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommentApiApplication.class, args);
	}

}
