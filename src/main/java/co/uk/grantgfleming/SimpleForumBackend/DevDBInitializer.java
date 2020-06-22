package co.uk.grantgfleming.SimpleForumBackend;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.ForumRepository;
import co.uk.grantgfleming.SimpleForumBackend.forum.Post;
import co.uk.grantgfleming.SimpleForumBackend.forum.PostRepository;
import co.uk.grantgfleming.SimpleForumBackend.users.User;
import co.uk.grantgfleming.SimpleForumBackend.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.h2.server.web.WebServlet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
@Profile("dev")
public class DevDBInitializer {
    @Bean
    CommandLineRunner initDatabase(PostRepository postRepository, ForumRepository forumRepository, UserRepository userRepository) {
        return args -> {
            log.info("Loading test database content.");

            User user = new User();
            user.setEmail("builtin@user.com");
            user.setAlias("built-in user");
            user.setRole(User.Role.USER);
            user = userRepository.save(user);

            Forum forum = new Forum();
            forum.setName("Forum Title");
            forum.setDescription("Forum Description");
            forum.setUser(user);
            forum = forumRepository.save(forum);

            Post post1 = new Post();
            post1.setTitle("First Post");
            post1.setBody("Some content here");
            post1.setForum(forum);
            post1.setCreator(user);
            postRepository.save(post1);

            Post post2 = new Post();
            post2.setTitle("Second Post");
            post2.setBody("Some more content here");
            post2.setForum(forum);
            post2.setCreator(user);
            postRepository.save(post2);
        };
    }

    /**
     * Initialize the h2 console in development so we can poke around the db
     */
    @Bean
    ServletRegistrationBean<WebServlet> h2servletRegistration() {
        ServletRegistrationBean<WebServlet> registrationBean = new ServletRegistrationBean<>(new WebServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }

}
