package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class BearerAuthenticationTokenTest {

    @Test
    void shouldHaveNullPrincipleAndEmptyAuthoritiesIfNoneAreGiven() {
        Authentication authentication = new BearerAuthenticationToken("some credentials");

        assertNull(authentication.getPrincipal());
        assertEquals(0, authentication.getAuthorities().size());
    }

    @Test
    void shouldBeSetAuthenticatedWhenCreatedWithPrincipleAndAuthorities() {
        Authentication authentication = new BearerAuthenticationToken(null, null, null);

        assertTrue(authentication.isAuthenticated());
    }

}