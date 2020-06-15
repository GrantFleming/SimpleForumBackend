package co.uk.grantgfleming.SimpleForumBackend;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.ForumRepository;
import co.uk.grantgfleming.SimpleForumBackend.forum.Post;
import co.uk.grantgfleming.SimpleForumBackend.forum.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DevDBInitializer {
    // TODO - only populate the database in certain contexts
    @Bean
    CommandLineRunner initDatabase(PostRepository postRepository, ForumRepository forumRepository) {
        return args -> {
            log.info("Loading test database content.");

            Forum forum = new Forum();
            forum.setName("Forum Title");
            forum.setDescription("Forum Description");
            forumRepository.save(forum);

            Post post1 = new Post();
            post1.setForumId(1L);
            post1.setTitle("First Post");
            post1.setBody("Some content here");
            postRepository.save(post1);

            Post post2 = new Post();
            post2.setForumId(1L);
            post2.setTitle("Second Post");
            post2.setBody("Some more content here");
            postRepository.save(post2);
        };
    }
}
