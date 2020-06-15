package co.uk.grantgfleming.SimpleForumBackend.security;

import co.uk.grantgfleming.SimpleForumBackend.security.JWT.JWTService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the security controller
 */
class SecurityControllerTest {

    JWTService jwtService = mock(JWTService.class);

    SecurityController controller = new SecurityController(jwtService);

    @AfterEach
    void tearDown() {
        // reset the authentication in the security context!
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    void shouldGenerateTheJWTFromTheAuthentication() {
        // given that there is an authentication set in the current security context
        Authentication testAuthentication = new TestingAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(testAuthentication);

        // when the jwt token is requested
        controller.getJWTToken();

        // then the jwt token is generated from the service
        verify(jwtService).generateJWT(testAuthentication);
    }
}