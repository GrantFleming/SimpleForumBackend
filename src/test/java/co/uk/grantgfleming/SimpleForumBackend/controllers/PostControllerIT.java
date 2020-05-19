package co.uk.grantgfleming.SimpleForumBackend.controllers;

import co.uk.grantgfleming.SimpleForumBackend.data_access.ForumRepository;
import co.uk.grantgfleming.SimpleForumBackend.data_access.Post;
import co.uk.grantgfleming.SimpleForumBackend.data_access.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * In preparation for a large code refactor the following integration test
 * is used primarily for regression testing at this time.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIT {

    @MockBean
    PostRepository mockPostRepository;
    @MockBean
    ForumRepository mockForumRepository;
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(mockPostRepository, mockForumRepository);
        // by default the forum repository says that every forum exists
        // this can be overridden by individual tests if necessary
        when(mockForumRepository.existsById(any())).thenReturn(true);
    }

    @Test
    void shouldGetAllPosts() throws Exception {
        // given there are three posts in the repository
        Post[] testPosts = {new Post(), new Post(), new Post()};
        String testPostsAsJson = mapper.writeValueAsString(testPosts);
        when(mockPostRepository.findByForumId(any())).thenReturn(Arrays.asList(testPosts));
        // when a get request is make to /posts with a valid forum id query string parameter
        // then all three posts are returned with a 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/posts?forumId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(testPostsAsJson));
    }

    @Test
    void shouldReturn400IfForumIdIsNotSpecified() throws Exception {
        // given anything
        // if a get request is made to /posts without a forumId query parameter
        // then a 400 is returned
        mvc.perform(MockMvcRequestBuilders.get("/posts").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400IfForumIdDoesntReferToExistingForum() throws Exception {
        // given that a forum with id 1 does not exist in the repository
        when(mockForumRepository.existsById(any())).thenReturn(false);
        // if a get request is made to /posts for all posts in forum with id 1
        // then a 400 is returned
        mvc.perform(MockMvcRequestBuilders.get("/posts?forumId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListOnNoPosts() throws Exception {
        // given there are no posts for forum with id 1
        // when a request is make to /posts for this forum
        // then an empty json list is returned with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/posts?forumId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldGetAPostThatExists() throws Exception {
        // given a post with id=3 exists
        Post testPost = new Post();
        testPost.setId(3L);
        String testPostAsJson = mapper.writeValueAsString(testPost);
        when(mockPostRepository.findById(3L)).thenReturn(of(testPost));
        // when a get request is made to /posts/3
        // then the post is returned with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/posts/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(testPostAsJson));
    }

    @Test
    void should404IfAPostDoesntExist() throws Exception {
        // given a post with id=3 does not exist
        when(mockPostRepository.findById(3L)).thenReturn(Optional.empty());
        // when a request is make to /posts/3
        // then a 404 is returned
        mvc.perform(MockMvcRequestBuilders.get("/posts/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateAPostIfSuppliedPostIsValid() throws Exception {
        // given the postRepository returns a newly created post with a generated id
        Post post = new Post();
        post.setId(3L);
        when(mockPostRepository.save(any())).thenReturn(post);
        // when a post request is made to /posts with a valid post in json format
        // valid here means correct format with no id set
        // then a 201 is returned along a the post object with id field generated
        String jsonPost = mapper.writeValueAsString(new Post()); // new post WITHOUT id
        String content = mvc.perform(MockMvcRequestBuilders.post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPost)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Post returnedPost = mapper.readValue(content, Post.class);
        assertEquals(3L, returnedPost.getId());
    }

    @Test
    void should400IfPostRefersToNonExistentForum() throws Exception {
        // given a forum with id 5 does not exist
        when(mockForumRepository.existsById(any())).thenReturn(false);
        // when a post request is made to /posts with a post that refers to forum 5
        // then a 400 is returned and it isn't added ot the repository

        Post post = new Post();
        post.setForumId(5L);
        String jsonPost = mapper.writeValueAsString(post);
        mvc.perform(MockMvcRequestBuilders.post("/posts").contentType(MediaType.APPLICATION_JSON).content(jsonPost))
                .andExpect(status().isBadRequest());

        verify(mockPostRepository, never()).save(any());

    }

    @Test
    void should400IfSuppliedPostAlreadyHasId() throws Exception {
        // given anything
        // when a post request is made where the post json object has an id
        // then a 400 is returned and it isn't added to the repository

        Post post = new Post();
        post.setId(1L);
        String jsonPost = mapper.writeValueAsString(post);
        mvc.perform(MockMvcRequestBuilders.post("/posts").contentType(MediaType.APPLICATION_JSON).content(jsonPost))
                .andExpect(status().isBadRequest());

        verify(mockForumRepository, never()).save(any());
    }
}