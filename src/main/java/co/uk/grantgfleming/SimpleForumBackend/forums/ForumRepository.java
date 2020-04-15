package co.uk.grantgfleming.SimpleForumBackend.forums;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumRepository extends JpaRepository<Forum, Long> {
}
