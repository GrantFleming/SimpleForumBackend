package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumService;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewForumException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * ForumController unit tests
 * <p>
 * For the most part this test just ensures a controller calls the correct service methods.
 */
class ForumControllerTest {

    ForumController controller;
    ForumService service;

    @BeforeEach
    void setUp() {
        service = mock(ForumService.class);
        controller = new ForumController(service);
    }

    @Test
    void getAllForumsShouldCallCorrectServiceMethod() {
        controller.getAllForums();
        verify(service, times(1)).allForums();
    }

    @Test
    void getForumsByIdShouldCallCorrectServiceMethod() {
        controller.getForumById(6L); // 6 is arbitrary
        verify(service, times(1)).findForumById(6L);
    }

    @Test
    void postNewForumShouldCallCorrectServiceMethod() {
        Forum testForum = new Forum();
        controller.postNewForum(testForum);
        verify(service, times(1)).addForum(testForum);
    }

    @Test
    void handlerShouldReturnExceptionMessage() {
        InvalidNewForumException ex = new InvalidNewForumException();
        String message = ex.getMessage();
        assertEquals(message, controller.invalidForumHandler(ex));
    }
}