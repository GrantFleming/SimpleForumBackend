package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.security.SecurityChainIT;
import co.uk.grantgfleming.SimpleForumBackend.users.ForumUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test that the security chain behaves as expected when certain user types try to access
 * various forum endpoints.
 * <p>
 * No testing of the controller itself should occur here! It is for testing the security chain
 * only in the context of handling requests that endeavour to reach the forum controller
 */
public class ForumSecurityIT extends SecurityChainIT {

    @MockBean
    ForumController forumController;

    /*
        Unauthenticated users should get a 200 when trying to access any endpoint at /api/forums*
        and the controller method should be called
     */
    @Test
    void should403OnUnauthenticatedUserAccess() throws Exception {
        request().endpoint("/api/forums").perform().andExpect(status().isOk());
        verify(forumController).getAllForums();

        request().endpoint("/api/forums/1").perform().andExpect(status().isOk());
        verify(forumController).getForumById(1L);
    }

    /*
        Authenticated user who do not have a role of "USER" should get a 403 when trying to POST
        to /api/forums and the controller method should not be called
     */
    @Test
    void should403OnNoUserRoleWhenPostingNewForum() throws Exception {
        request().endpoint("/api/forums")
                .method(HttpMethod.POST)
                .authenticated()
                .perform()
                .andExpect(status().isForbidden());
        verifyNoInteractions(forumController);
    }

    /*
        Authenticated users who do not have a role "USER" should call through to the controller
        when GET-ing from any endpoint at /api/forums*
     */
    @Test
    void shouldCallThroughToControllerIfAuthenticated() throws Exception {
        request().endpoint("/api/forums").authenticated().perform();
        verify(forumController).getAllForums();
        reset(forumController);

        request().endpoint("/api/forums/1").authenticated().perform();
        verify(forumController).getForumById(1L);
    }

    /*
        Authenticated users with a role of "USER" should successfully call through the the controller
        method when POST-ing to /api/forums
     */
    @Test
    void shouldCallThroughIfAuthenticatedWithRoleUser() throws Exception {
        request().endpoint("/api/forums")
                .method(HttpMethod.POST)
                .authenticated()
                .withRole(ForumUser.Role.USER)
                .perform();

        verify(forumController).postNewForum(any());
    }
}
