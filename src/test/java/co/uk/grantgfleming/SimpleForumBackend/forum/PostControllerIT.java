package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.users.User;
import co.uk.grantgfleming.SimpleForumBackend.users.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the /api/posts endpoints
 * <p>
 * All testing assumes a valid, authenticated user who is authorized to do what is required
 * of the test.
 * <p>
 * Security is tested elsewhere.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIT {

    @MockBean
    PostRepository mockPostRepository;
    @MockBean
    ForumRepository mockForumRepository;

    @MockBean
    UserRepository userRepository;
    User user;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        // by default the forum repository says that every forum exists
        // this can be overridden by individual tests if necessary
        when(mockForumRepository.existsById(any())).thenReturn(true);

        // ensure the userRepository is able to return a corresponding
        //  co.uk.grantgfleming.SimpleForumBackend.users.User object for the
        // @WithMockUser's Authenticated principle
        user = new User();
        user.setAlias("some alias");
        user.setEmail("user");
        when(userRepository.findByEmail(any())).thenReturn(of(user));
    }

    @Test
    @WithMockUser
    void shouldGetAllPosts() throws Exception {
        // given there are three posts in the repository
        Post[] testPosts = {new Post(), new Post(), new Post()};
        Arrays.stream(testPosts).forEach(post -> post.setForum(new Forum()));
        Arrays.stream(testPosts).forEach(post -> post.setCreator(new User()));
        when(mockPostRepository.findByForumId(any())).thenReturn(Arrays.asList(testPosts));

        // when a get request is make to api/posts with a valid forum id query string parameter
        // then all three posts are returned with a 200 OK
        List<PostDTO> expectedReturnedPosts = Arrays.stream(testPosts)
                .map(PostDTO::fromPost)
                .collect(Collectors.toList());
        String expectedPostsAsJson = mapper.writeValueAsString(expectedReturnedPosts);
        mvc.perform(MockMvcRequestBuilders.get("/api/posts?forumId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedPostsAsJson));
    }

    @Test
    @WithMockUser
    void shouldReturn400IfForumIdIsNotSpecified() throws Exception {
        // given anything
        // if a get request is made to api/posts without a forumId query parameter
        // then a 400 is returned
        mvc.perform(MockMvcRequestBuilders.get("/api/posts").accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturn400IfForumIdDoesntReferToExistingForum() throws Exception {
        // given that a forum with id 1 does not exist in the repository
        when(mockForumRepository.existsById(any())).thenReturn(false);
        // if a get request is made to api/posts for all posts in forum with id 1
        // then a 400 is returned
        mvc.perform(MockMvcRequestBuilders.get("/api/posts?forumId=1").accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListOnNoPosts() throws Exception {
        // given there are no posts for forum with id 1
        // when a request is make to api/posts for this forum
        // then an empty json list is returned with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/api/posts?forumId=1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser
    void shouldGetAPostThatExists() throws Exception {
        // given a post with id=3 exists
        Post testPost = new Post();
        testPost.setCreator(new User());
        testPost.setForum(new Forum());
        testPost.setId(3L);
        when(mockPostRepository.findById(3L)).thenReturn(of(testPost));
        String expectedReturnedPostAsJson = mapper.writeValueAsString(PostDTO.fromPost(testPost));
        // when a get request is made to api/posts/3
        // then the post is returned with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/api/posts/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedReturnedPostAsJson));
    }

    @Test
    @WithMockUser
    void should404IfAPostDoesntExist() throws Exception {
        // given a post with id=3 does not exist
        when(mockPostRepository.findById(3L)).thenReturn(Optional.empty());
        // when a request is make to api/posts/3
        // then a 404 is returned
        mvc.perform(MockMvcRequestBuilders.get("/api/posts/3").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldCreateAPostIfSuppliedPostIsValid() throws Exception {
        // given that the postRepository returns a newly created post with a generated id
        Post post = new Post();
        post.setId(3L);
        post.setForum(new Forum());
        post.setCreator(new User());
        when(mockPostRepository.save(any())).thenReturn(post);
        when(mockForumRepository.findById(1L)).thenReturn(of(new Forum()));
        // when a post request is made to api/posts with a valid post in json format
        // valid here means correct format with no id set
        // then a 201 is returned along a the post object with id field generated
        PostDTO requestedNewPost = new PostDTO();
        requestedNewPost.setForumId(1L);
        String jsonPost = mapper.writeValueAsString(requestedNewPost); // new post WITHOUT id
        String content = mvc.perform(MockMvcRequestBuilders.post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPost)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        PostDTO returnedPost = mapper.readValue(content, PostDTO.class);
        assertEquals(3L, returnedPost.getId());
    }

    @Test
    @WithMockUser
    void should400IfPostRefersToNonExistentForum() throws Exception {
        // and a forum with id 5 does not exist
        when(mockForumRepository.existsById(any())).thenReturn(false);
        // when a post request is made to api/posts with a post that refers to forum 5
        // then a 400 is returned and it isn't added to the repository

        PostDTO post = new PostDTO();
        post.setForumId(5L);
        String jsonPost = mapper.writeValueAsString(post);
        mvc.perform(MockMvcRequestBuilders.post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(jsonPost))
                .andExpect(status().isBadRequest());

        verify(mockPostRepository, never()).save(any());

    }

    @Test
    @WithMockUser
    void should400IfSuppliedPostAlreadyHasId() throws Exception {
        // given anything
        // when a post request is made where the post json object has an id
        // then a 400 is returned and it isn't added to the repository

        Post post = new Post();
        post.setId(1L);
        String jsonPost = mapper.writeValueAsString(post);
        mvc.perform(MockMvcRequestBuilders.post("/api/posts").contentType(MediaType.APPLICATION_JSON).content(jsonPost))
                .andExpect(status().isBadRequest());

        verify(mockForumRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void shouldAddTheCurrentlyAuthenticatedUsersAliasToTheReturnedForumDTO() throws Exception {
        when(mockPostRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(mockForumRepository.findById(1L)).thenReturn(of(new Forum()));

        // given that a user is authenticated
        // when a new forum is created
        PostDTO postDTO = new PostDTO();
        postDTO.setForumId(1L);
        String jsonPost = mapper.writeValueAsString(postDTO);
        String content = mvc.perform(MockMvcRequestBuilders.post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPost)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        // the returned ForumDTO contains the authenticated users alias
        PostDTO returnedPost = mapper.readValue(content, PostDTO.class);
        assertEquals(user.getAlias(), returnedPost.getCreator());
    }
}