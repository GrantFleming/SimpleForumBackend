package co.uk.grantgfleming.SimpleForumBackend.users;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    UserRepository userRepository = mock(UserRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    UserController controller = new UserController(userRepository, passwordEncoder);

    @Test
    void shouldThrowIfEmailExists() {
        // given that a user with a certain email already exists in the repository
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // when an attempt is made to register a new user with this email
        // then a UserAlreadyExistsException is thrown
        assertThrows(UserAlreadyExistsException.class, () -> controller.register("someemail", "somepassword"));
    }

    @Test
    void shouldSaveIfEmailDoesNotExist() throws Exception {
        // given that no user with a certain email exists in the repository
        when(userRepository.existsByEmail(any())).thenReturn(false);

        // when an attempt is made to register a new user with this email
        String username = "some email";
        controller.register(username, "somepassword");

        // then a user with this email is saved to the repository
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
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
    void shouldReturnExceptionMessage() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("someone");
        String message = ex.getMessage();
        assertEquals(message, controller.userAlreadyExistsHandler(ex));
    }
}