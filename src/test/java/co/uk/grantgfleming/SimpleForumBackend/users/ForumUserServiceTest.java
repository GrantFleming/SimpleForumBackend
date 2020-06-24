package co.uk.grantgfleming.SimpleForumBackend.users;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ForumUserServiceTest {

    UserRepository repository = mock(UserRepository.class);
    UserService userService = new UserService(repository);

    @Test
    void shouldThrowUserNotFoundExceptionIfNotUserExists() {
        // given that no user with a certain email exists in the repository
        when(repository.findByEmail(any())).thenReturn(Optional.empty());

        // when an attempt to get that user is made
        // then a UserNotFoundException is thrown
        assertThrows(UserNotFoundException.class, () -> userService.getUser("some email"));
    }

    @Test
    void shouldReturnUserFromRepositoryIfExists() throws Exception {
        // given that a user with a certain email exists in the repository
        ForumUser forumUser = new ForumUser();
        when(repository.findByEmail("some user email")).thenReturn(of(forumUser));

        // when an attempt to get that user is made
        ForumUser returnedForumUser = userService.getUser("some user email");

        // then the user is returned
        assertEquals(forumUser, returnedForumUser);
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionIfNoUserExists() {
        // given that no user with a certain email exists in the repository
        when(repository.findByEmail(any())).thenReturn(Optional.empty());

        // when an attempt to get that user is made throw the UserDetailsService interface
        // then a UsernameNotFoundException is thrown
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("some email"));
    }

    @Test
    void shouldReturnUserWithCorrectEmailAndRolesIfUserExists() {
        // given that a user with a certain email exists in the repository
        ForumUser forumUser = new ForumUser();
        forumUser.setEmail("some user email");
        forumUser.setPassword("any old password");
        forumUser.setRole(ForumUser.Role.NONE);
        when(repository.findByEmail("some user email")).thenReturn(of(forumUser));

        // when an attempt to get that user is made through the UserDetailsService interface
        UserDetails userDetails = userService.loadUserByUsername("some user email");

        // then a UserDetails object with that email is returned
        assertEquals(forumUser.getEmail(), userDetails.getUsername());
    }
}