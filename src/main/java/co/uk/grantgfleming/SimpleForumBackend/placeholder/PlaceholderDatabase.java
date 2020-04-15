package co.uk.grantgfleming.SimpleForumBackend.placeholder;

import co.uk.grantgfleming.SimpleForumBackend.forums.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forums.ForumRepository;
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
    CommandLineRunner initDatabase(PostRepository postRepository, ForumRepository forumRepository) {
        return args -> {
            log.info("Preloading " + forumRepository.save(new Forum("Forum Title", "Forum Description")));
            log.info("Preloading " + postRepository.save(new Post(1L, "First Post", "Some content here")));
            log.info("Preloading " + postRepository.save(new Post(1L, "First Post", "Some content here")));
        };
    }
}
