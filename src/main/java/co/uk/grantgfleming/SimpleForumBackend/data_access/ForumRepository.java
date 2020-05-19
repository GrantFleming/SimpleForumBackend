package co.uk.grantgfleming.SimpleForumBackend.data_access;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumRepository extends JpaRepository<Forum, Long> {
}
