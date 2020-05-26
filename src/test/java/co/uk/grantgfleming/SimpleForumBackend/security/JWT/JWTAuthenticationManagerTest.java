package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import co.uk.grantgfleming.SimpleForumBackend.users.User;
import co.uk.grantgfleming.SimpleForumBackend.users.UserNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTAuthenticationManagerTest {

    JWTService jwtService = mock(JWTService.class);
    UserService userService = mock(UserService.class);
    JWTAuthenticationManager jwtAuthManager = new JWTAuthenticationManager(jwtService, userService);

    @Test
    void shouldThrowIfCredentialsAreInvalid() {
        // given that the credentials are invalid
        when(jwtService.validateJWT(any())).thenThrow(JWTVerificationException.class);
        Authentication auth = new BearerAuthenticationToken("invalid credentials");

        // when authentication is attempted
        // a BadCredentialsException is thrown
        assertThrows(BadCredentialsException.class, () -> jwtAuthManager.authenticate(auth));
    }

    @Test
    void shouldThrowIfNoUserExists() throws Exception {
        // given that a JWT supplies a username that no longer exists
        when(jwtService.validateJWT(anyString())).thenReturn("aUsername");
        Authentication auth = new BearerAuthenticationToken("valid credentials");
        when(userService.getUser(eq("aUsername"))).thenThrow(UserNotFoundException.class);

        // when authentication is attempted using this token
        // a ProviderNotFoundException is thrown
        assertThrows(ProviderNotFoundException.class, () -> jwtAuthManager.authenticate(auth));
    }

    @Test
    void shouldThrowIfAuthenticationIsNotBearerAuthenticationToken() {
        // given that an Authentication is not an instance of BearerAuthenticationToken
        Authentication auth = new TestingAuthenticationToken(null, null);

        // when authentication is attempted using this token
        // then an IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> jwtAuthManager.authenticate(auth));
    }

    @Test
    void shouldSetAUserAsPrinciple() throws Exception {
        // given that a user exists
        User aUser = new User();
        when(userService.getUser(eq("aUsername"))).thenReturn(aUser);

        // when authentication is attempted on providing their username
        when(jwtService.validateJWT(anyString())).thenReturn("aUsername");
        Authentication auth = new BearerAuthenticationToken("valid credentials");
        Authentication resultAuth = jwtAuthManager.authenticate(auth);

        // then the user is set as the principle
        assertEquals(aUser, resultAuth.getPrincipal());
    }

    @Test
    void shouldSetJWTTokenAsCredential() throws Exception {
        // given that a user exists
        User aUser = new User();
        when(userService.getUser(eq("aUsername"))).thenReturn(aUser);

        // when they are authenticated successfully
        String token = "some valid JWT token";
        when(jwtService.validateJWT(anyString())).thenReturn("aUsername");
        Authentication auth = new BearerAuthenticationToken(token);
        Authentication resultAuth = jwtAuthManager.authenticate(auth);

        // then the original JWT used for authentication is set as the credentials
        assertEquals(token, resultAuth.getCredentials());
    }

    @Test
    void shouldAssignGrantedAuthorityFromUserRole() throws Exception {
        // given that a user has a certain role
        User.Role role = User.Role.USER;
        User aUser = new User();
        aUser.setRole(role);
        when(userService.getUser(eq("aUsername"))).thenReturn(aUser);

        // when they are authenticated successfully
        when(jwtService.validateJWT(anyString())).thenReturn("aUsername");
        Authentication auth = new BearerAuthenticationToken("some valid JWT");
        Authentication resultAuth = jwtAuthManager.authenticate(auth);

        // then a GrantedAuthority for the given user role is set
        Collection<? extends GrantedAuthority> authorities = resultAuth.getAuthorities();
        assertTrue(authorities.stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role.name())));
    }

}