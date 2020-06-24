package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumService;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewForumException;
import co.uk.grantgfleming.SimpleForumBackend.users.ForumUser;
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
    ForumService mockForumService;

    @BeforeEach
    void setUp() {
        mockForumService = mock(ForumService.class);

        // by default for these tests we have the forumService return empty forums
        // to avoid NullPointerExceptions
        Forum serviceReturnedForum = new Forum();
        serviceReturnedForum.setForumUser(new ForumUser());
        when(mockForumService.addForum(any())).thenReturn(serviceReturnedForum);
        when(mockForumService.findForumById(any())).thenReturn(serviceReturnedForum);

        controller = new ForumController(mockForumService);
    }

    @Test
    void getAllForumsShouldCallCorrectServiceMethod() {
        controller.getAllForums();
        verify(mockForumService, times(1)).allForums();
    }

    @Test
    void getForumsByIdShouldCallCorrectServiceMethod() {
        controller.getForumById(6L); // 6 is arbitrary
        verify(mockForumService, times(1)).findForumById(6L);
    }

    @Test
    void postNewForumShouldCallCorrectServiceMethod() {
        controller.postNewForum(new ForumDTO());
        verify(mockForumService, times(1)).addForum(any());
    }

    @Test
    void handlerShouldReturnExceptionMessage() {
        InvalidNewForumException ex = new InvalidNewForumException();
        String message = ex.getMessage();
        assertEquals(message, controller.invalidForumHandler(ex));
    }
}