package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.Post;
import co.uk.grantgfleming.SimpleForumBackend.forum.PostDTO;
import co.uk.grantgfleming.SimpleForumBackend.forum.PostRepository;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

abstract class PostServiceTest {

    private PostService sut;
    private PostRepository mockRepo;
    private ForumService mockForumService;
    private UserService mockUserService;

    public abstract PostService createInstance(PostRepository repository, ForumService forumService, UserService userService);

    @BeforeEach
    void setUp() {
        mockRepo = mock(PostRepository.class);
        mockForumService = mock(ForumService.class);
        mockUserService = mock(UserService.class);
        sut = createInstance(mockRepo, mockForumService, mockUserService);
    }

    @Test
    void shouldReturnAllPostsThatExistForAForum() {
        // given that a forum exists and contains posts
        when(mockForumService.existsById(2L)).thenReturn(true);
        Post[] testPosts = {new Post(), new Post(), new Post()};
        when(mockRepo.findByForumId(2L)).thenReturn(Arrays.asList(testPosts));

        // when these posts are requested
        List<Post> returnedPosts = sut.getPostsForForum(2L);

        // then they are successfully returned
        assertArrayEquals(testPosts, returnedPosts.toArray());
    }

    @Test
    void shouldThrowForumNotFoundIfNoForumExistsForGivenForumId() {
        // given that no forum exists for a given id
        when(mockForumService.existsById(2L)).thenReturn(false);

        // when the posts for this forum are requested
        // then a ForumNotFoundException is thrown
        assertThrows(ForumNotFoundException.class, () -> sut.getPostsForForum(2L));
    }

    @Test
    void shouldReturnEmptyListIfNoPostsExistForAGivenForum() {
        // given that a forum exists but there are no posts in it
        when(mockForumService.existsById(7L)).thenReturn(true);
        when(mockRepo.findByForumId(7L)).thenReturn(Collections.emptyList());

        // when the posts for this forum are requested
        List<Post> returnedPosts = sut.getPostsForForum(7L);

        // then an empty list is returned
        assertTrue(returnedPosts.isEmpty());
    }

    @Test
    void shouldReturnAPostIfOneExistsWithTheGivenId() {
        // given that a post exists with a certain id
        Post testPost = new Post();
        when(mockRepo.findById(4L)).thenReturn(of(testPost));

        // when this post is requested
        Post returnedPost = sut.findPostById(4L);

        // then it is returned
        assertEquals(testPost, returnedPost);
    }

    @Test
    void shouldThrowPostNotFoundExceptionIfNoPostExistsWithGivenId() {
        // given that no post exists with a certain id
        when(mockRepo.findById(3L)).thenReturn(Optional.empty());

        // when this post is requested
        // then a PostNotFoundException is thrown
        assertThrows(PostNotFoundException.class, () -> sut.findPostById(3L));

    }

    @Test
    void shouldAddAValidPostToTheUnderlyingRepository() {
        // given that a new post is valid
        // valid here means does not have an id already allocated
        // and refers to a forum that exists
        PostDTO testPost = new PostDTO();
        testPost.setTitle("some title");
        testPost.setBody("some body");
        testPost.setForumId(1L);
        when(mockForumService.existsById(1L)).thenReturn(true);
        Forum returnedForum = new Forum();
        returnedForum.setId(1L);
        when(mockForumService.findForumById(1L)).thenReturn(returnedForum);

        // when is is added to the service
        sut.addPost(testPost);

        // then it is added to the underlying repository
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(mockRepo, times(1)).save(postCaptor.capture());
        Post addedPost = postCaptor.getValue();
        assertEquals(addedPost.getForum().getId(), testPost.getForumId());
        assertEquals(addedPost.getTitle(), testPost.getTitle());
        assertEquals(addedPost.getBody(), testPost.getBody());
    }

    @Test
    void shouldReturnANewlyAddedPostOnSuccessfulEntryToRepository() {
        // given that a new post is valid
        // valid here means does not have an id already allocated
        // and refers to a forum that exists
        PostDTO testPost = new PostDTO();
        testPost.setForumId(1L);
        when(mockForumService.existsById(1L)).thenReturn(true);
        Post repoReturnedPost = new Post();
        // not necessarily the same Post object need be returned!
        when(mockRepo.save(any())).thenReturn(repoReturnedPost);

        // when it is added to the service
        Post returnedPost = sut.addPost(testPost);

        // then the new post is also returned again
        assertEquals(repoReturnedPost, returnedPost);
    }

    @Test
    void shouldThrowInvalidNewPostExceptionIfNewPostRefersToNonExistentForum() {
        // given that a new post refers to a forum that does not exist
        when(mockForumService.existsById(9L)).thenReturn(false);
        when(mockForumService.findForumById(9L)).thenThrow(new ForumNotFoundException(9L));
        PostDTO post = new PostDTO();
        post.setForumId(9L);

        // when this post is added to the service
        // then a ForumNotFoundException is thrown
        assertThrows(ForumNotFoundException.class, () -> sut.addPost(post));
    }
}