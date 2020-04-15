package co.uk.grantgfleming.SimpleForumBackend.placeholder;

import co.uk.grantgfleming.SimpleForumBackend.posts.Post;
import co.uk.grantgfleming.SimpleForumBackend.posts.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PlaceholderDatabase {

    @Bean
    CommandLineRunner initDatabase(PostRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Post("First Post", "Some content here")));
            log.info("Preloading " + repository.save(new Post("Second Post", "Some more content here")));
        };
    }
}
