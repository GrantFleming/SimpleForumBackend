package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewPostException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.PostNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * {@link PostController} unit tests
 * <p>
 * For the most part this test just ensures that a controller calls the correct service method.
 */
class PostControllerTest {

    PostController controller;
    PostService service;

    @BeforeEach
    void setUp() {
        service = mock(PostService.class);
        controller = new PostController(service);
    }

    @Test
    void getPostsByForumIdShouldCallCorrectServiceMethod() {
        controller.getPostsForForum(9L);
        verify(service, times(1)).getPostsForForum(9L);
    }

    @Test
    void getPostsByIdShouldCallCorrectServiceMethod() {
        controller.getPostById(6L);
        verify(service, times(1)).findPostById(6L);
    }

    @Test
    void postNewPostShouldCallCorrectServiceMethod() {
        Post post = new Post();
        controller.postNewPost(post);
        verify(service, times(1)).addPost(post);
    }

    @Test
    void postNotFoundHandlerShouldReturnMessage() {
        PostNotFoundException ex = new PostNotFoundException(4L);
        assertEquals(ex.getMessage(), controller.postNotFoundHandler(ex));
    }

    @Test
    void invalidNewPostHandlerShouldReturnMessage() {
        InvalidNewPostException ex = new InvalidNewPostException();
        assertEquals(ex.getMessage(), controller.invalidNewPostHandler(ex));
    }

    @Test
    void forumNotFoundHandlerShouldReturnMessage() {
        ForumNotFoundException ex = new ForumNotFoundException(4L);
        assertEquals(ex.getMessage(), controller.forumNotFoundHandler(ex));
    }
}