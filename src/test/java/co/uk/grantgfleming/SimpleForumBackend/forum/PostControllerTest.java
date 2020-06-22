package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewPostException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.PostNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.PostService;
import co.uk.grantgfleming.SimpleForumBackend.users.User;
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
    PostService mockPostService;

    @BeforeEach
    void setUp() {
        mockPostService = mock(PostService.class);

        // By default we have the mockPostService return just enough Post to avoid
        // NullPointerExceptions in the tests
        Post returnedPost = new Post();
        returnedPost.setCreator(new User());
        returnedPost.setForum(new Forum());
        when(mockPostService.addPost(any())).thenReturn(returnedPost);
        when(mockPostService.findPostById(any())).thenReturn(returnedPost);

        controller = new PostController(mockPostService);
    }

    @Test
    void getPostsByForumIdShouldCallCorrectServiceMethod() {
        controller.getPostsForForum(9L);
        verify(mockPostService, times(1)).getPostsForForum(9L);
    }

    @Test
    void getPostsByIdShouldCallCorrectServiceMethod() {
        controller.getPostById(6L);
        verify(mockPostService, times(1)).findPostById(6L);
    }

    @Test
    void postNewPostShouldCallCorrectServiceMethod() {
        PostDTO post = new PostDTO();
        controller.postNewPost(post);
        verify(mockPostService, times(1)).addPost(post);
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