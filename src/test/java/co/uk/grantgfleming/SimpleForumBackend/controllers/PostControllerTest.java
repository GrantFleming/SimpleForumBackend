package co.uk.grantgfleming.SimpleForumBackend.controllers;

import co.uk.grantgfleming.SimpleForumBackend.data_access.Post;
import co.uk.grantgfleming.SimpleForumBackend.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

}