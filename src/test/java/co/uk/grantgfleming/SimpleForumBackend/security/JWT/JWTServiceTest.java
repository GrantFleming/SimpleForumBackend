package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTServiceTest {

    Clock mockClock = mock(Clock.class);
    JWTService jwtService = new JWTService(mockClock);

    @Test
    void shouldValidateAndReturnSubjectIfGeneratedWithSubject() {
        when(mockClock.millis()).thenReturn(Clock.systemDefaultZone().millis());

        // given that a JWT is generated from a provided String subject
        String providedSubject = "any old subject";
        String token = jwtService.generateJWT(providedSubject);

        // when that JWT is verified
        String extractedSubject = jwtService.validateJWT(token);

        // then the same subject is returned
        assertEquals(extractedSubject, providedSubject);
    }

    @Test
    void shouldValidateAndReturnSubjectIfGeneratedWithAuthentication() {
        when(mockClock.millis()).thenReturn(Clock.systemDefaultZone().millis());

        // given that a JWT is generated from an Authentication
        String principleName = "some principle";
        Authentication authentication = new TestingAuthenticationToken(principleName, null);
        String token = jwtService.generateJWT(authentication);

        // when that JWT is verified
        String extractedSubject = jwtService.validateJWT(token);

        // then the subject returned is the Authentication principle's name
        assertEquals(principleName, extractedSubject);
    }

    @Test
    void shouldFailToVerifyInvalidSignature() {
        when(mockClock.millis()).thenReturn(Clock.systemDefaultZone().millis());

        // given that a JWT has an invalid signature
        String token = jwtService.generateJWT("anything");
        // invalidate by changing a character in the signature
        StringBuilder invalidToken = new StringBuilder(token);
        invalidToken.append('x'); // now the signature is invalid

        // when that JWT is verified
        // then a SignatureVerificationException is thrown
        assertThrows(SignatureVerificationException.class, () -> jwtService.validateJWT(invalidToken.toString()));
    }

    @Test
    void shouldFailToVerifyIfExpired() {
        // given that a JWT has expired
        when(mockClock.millis()).thenReturn(Clock.systemDefaultZone().millis() - 36000001);
        String token = jwtService.generateJWT("any subject");

        // when that JWT is verified
        // then a TokenException is thrown
        assertThrows(TokenExpiredException.class, () -> jwtService.validateJWT(token));
    }
}