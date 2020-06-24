package co.uk.grantgfleming.SimpleForumBackend.users;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ForumUserControllerTest {

    UserRepository userRepository = mock(UserRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    UserController controller = new UserController(userRepository, passwordEncoder);

    @Test
    void shouldThrowIfEmailExists() {
        // given that a user with a certain email already exists in the repository
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when an attempt is made to register a new user with this email
        // then a UserAlreadyExistsException is thrown
        assertThrows(UserAlreadyExistsException.class, () -> controller.register("someemail", "somealias", "somepassword"));
    }

    @Test
    void shouldThrowIfAliasExists() {
        // given that a user with a certain alias already exists in the repository
        when(userRepository.existsByAlias(any())).thenReturn(true);

        // when an attempt is make to register a new user with this alias
        // then a UserAlreadyExistsException is thrown
        String alias = "someExistingAlias";
        assertThrows(UserAlreadyExistsException.class, () -> controller.register("some email", alias, "somepassword"));
    }

    @Test
    void shouldSaveIfEmailDoesNotExist() throws Exception {
        // given that no user with a certain email exists in the repository
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(new ForumUser());

        // when an attempt is made to register a new user with this email
        String username = "some email";
        controller.register(username, "someuniquealias", "somepassword");

        // then a user with this email is saved to the repository
        ArgumentCaptor<ForumUser> argument = ArgumentCaptor.forClass(ForumUser.class);
        verify(userRepository).save(argument.capture());
        assertEquals(username, argument.getValue().getEmail());
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        // given that a user with a certain email exists in the repository
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when the existence of the email is queried
        // then it should return true
        assertTrue(controller.emailExists("something"));
    }

    @Test
    void shouldReturnFalseIfEmailDoesnNotExist() {
        // given that no user with a certain email exists in the repository
        when(userRepository.existsByEmail(any())).thenReturn(false);

        // when the existence of the email is queried
        // then it should return false
        assertFalse(controller.emailExists("something"));
    }

    @Test
    void shouldReturnTrueIfAliasExistsWhenValidatingAlias() {
        // given that a user with a certain alias exists in the repository
        when(userRepository.existsByAlias(any())).thenReturn(true);

        // when the existence of the alias is queried
        // then it should return true
        assertTrue(controller.aliasExists("something"));
    }

    @Test
    void shouldReturnFalseIfAliasDoesNotExistWhenValidatingAlias() {
        // given that a user with a certain alias does not exist in the repository
        when(userRepository.existsByAlias(any())).thenReturn(false);

        // when the existence of the alias is queried
        // then it should return false
        assertFalse(controller.aliasExists("something"));
    }

    @Test
    void shouldReturnExceptionMessage() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("someone");
        String message = ex.getMessage();
        assertEquals(message, controller.userAlreadyExistsHandler(ex));
    }
}