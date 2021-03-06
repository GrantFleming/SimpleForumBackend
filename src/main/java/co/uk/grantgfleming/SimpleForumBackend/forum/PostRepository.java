package co.uk.grantgfleming.SimpleForumBackend.forum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByForumId(Long forumId);

}
