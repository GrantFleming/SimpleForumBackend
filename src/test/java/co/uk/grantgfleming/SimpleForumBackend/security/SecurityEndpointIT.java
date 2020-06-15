package co.uk.grantgfleming.SimpleForumBackend.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test that the security chain behaves as expected when certain user types try to access
 * various security endpoints.
 * <p>
 * No testing of the controller itself should occur here! It is for testing the security chain
 * only in the context of handling requests that endeavour to reach the security controller
 */
public class SecurityEndpointIT extends SecurityChainIT {

    @MockBean
    SecurityController securityController;

    /*
        Unauthenticated users should get a 401 when trying to access /auth/token
        instructing them to use basic authentication
     */
    @Test
    void should401OnUnauthenticatedUserAccess() throws Exception {
        request().endpoint("/auth/token")
                .perform()
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", startsWith("Basic ")));
        verifyNoInteractions(securityController);
    }

    /*
        Any authenticated user should call through to the controller when trying to
        access /auth/token
     */
    @Test
    void shouldCallThroughToControllerIfAuthenticated() throws Exception {
        request().endpoint("/auth/token").authenticated().withBasic().perform();
        verify(securityController).getJWTToken();
    }

}
