package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.security.SecurityChainIT;
import co.uk.grantgfleming.SimpleForumBackend.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test that the security chain behaves as expected when certain user types try to access
 * various forum post endpoints.
 * <p>
 * No testing of the controller itself should occur here! It is for testing the security chain
 * only in the context of handling requests that endeavour to reach the post controller
 */
public class PostSecurityIT extends SecurityChainIT {

    @MockBean
    PostController postController;

    /*
        Unauthenticated users should get a 200 when trying to GET from any endpoint at /api/posts*
        and the controller method should be called
     */
    @Test
    void should403OnUnauthenticatedUserAccess() throws Exception {
        request().endpoint("/api/posts?forumId=1").perform().andExpect(status().isOk());
        verify(postController).getPostsForForum(1L);

        request().endpoint("/api/posts/1").perform().andExpect(status().isOk());
        verify(postController).getPostById(1L);
    }


    /*
        Authenticated user who do not have a role of "USER" should get a 403 when trying to POST
        to /api/posts and the controller method should not be called
     */
    @Test
    void should403OnNoUserRoleWhenPostingNewForum() throws Exception {
        request().endpoint("/api/posts").method(HttpMethod.POST).authenticated().perform().andExpect(status().isForbidden());
        verifyNoInteractions(postController);
    }

    /*
        Authenticated users who do not have a role "USER" should call through to the controller
        when GET-ing from any endpoint at /api/posts*
     */
    @Test
    void shouldCallThroughToControllerIfAuthenticated() throws Exception {
        request().endpoint("/api/posts?forumId=1").authenticated().perform();
        verify(postController).getPostsForForum(1L);
        reset(postController);

        request().endpoint("/api/posts/1").authenticated().perform();
        verify(postController).getPostById(1L);
    }

    /*
        Authenticated users with a role of "USER" should successfully call through the the controller
        method when POST-ing to /api/posts
     */
    @Test
    void shouldCallThroughIfAuthenticatedWithRoleUser() throws Exception {
        request().endpoint("/api/posts").method(HttpMethod.POST).authenticated().withRole(User.Role.USER).perform();

        verify(postController).postNewPost(any());
    }
}
