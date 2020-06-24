package co.uk.grantgfleming.SimpleForumBackend.users;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<ForumUser, Long> {
    Optional<ForumUser> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByAlias(String alias);
}
