package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.users.ForumUser;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the /api/forum endpoints
 * <p>
 * All testing assumes a valid, authenticated user who is authorized to do what is required
 * of the test.
 * <p>
 * Security is tested elsewhere.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ForumControllerIT {

    @MockBean
    ForumRepository mockForumRepository;
    @MockBean
    UserRepository userRepository;
    ForumUser forumUser;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        forumUser = new ForumUser();
        forumUser.setAlias("some alias");
        when(userRepository.findByEmail(any())).thenReturn(of(forumUser));
    }

    @Test
    @WithMockUser
    void shouldGetAllForums() throws Exception {
        // given there are three forums in the repository
        Forum[] testForums = {new Forum(), new Forum(), new Forum()};
        Arrays.stream(testForums).forEach(forum -> forum.setForumUser(forumUser));
        List<ForumDTO> forumDTOs = Arrays.stream(testForums).map(ForumDTO::fromForum).collect(Collectors.toList());

        String testForumsAsJson = mapper.writeValueAsString(forumDTOs);
        when(mockForumRepository.findAll()).thenReturn(Arrays.asList(testForums));

        // when a get request is made to api/forums for json
        // then all three forums are returned along with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/api/forums").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(testForumsAsJson));

        verify(mockForumRepository, times(1)).findAll();
    }

    @Test
    @WithMockUser
    void shouldReturnEmptyListOnNoForums() throws Exception {
        // given there are no forums in the repository
        // when a get request is made to api/forums
        // then a json empty list is retuned with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/api/forums").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(mockForumRepository, times(1)).findAll();
    }

    @Test
    @WithMockUser
    void shouldGetAForumThatExists() throws Exception {
        // given there is a forum with id=1 in the repository
        Forum forum = new Forum();
        forum.setForumUser(forumUser);
        String forumAsJson = mapper.writeValueAsString(ForumDTO.fromForum(forum));
        when(mockForumRepository.findById(1L)).thenReturn(of(forum));

        // when a get request is made to api/forums/1
        // then the forum is returned along with 200 OK
        mvc.perform(MockMvcRequestBuilders.get("/api/forums/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(forumAsJson));
        verify(mockForumRepository, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void shouldReturn404IfAForumDoesntExist() throws Exception {
        // given there is no forum with id=1
        when(mockForumRepository.findById(1L)).thenReturn(Optional.empty());
        // when a get request is made to api/forums/1
        // then a 404 is returned with no body
        mvc.perform(MockMvcRequestBuilders.get("/api/forums/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(emptyString()));
        verify(mockForumRepository, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void shouldCreateAForumIfItDoesntAlreadyExist() throws Exception {
        // and the forumRepository returns a newly created forum with a generated id on save
        Forum forum = new Forum();
        forum.setId(6L);
        forum.setForumUser(forumUser);
        when(mockForumRepository.save(any())).thenReturn(forum);

        // when a post request is make to api/forums with a valid forum in json format
        // (valid here means correct format but with no set id)
        //then a 201 is returned along with the forum object with the id field generated
        String jsonForum = mapper.writeValueAsString(new Forum());
        String content = mvc.perform(MockMvcRequestBuilders.post("/api/forums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForum)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        ForumDTO returnedForum = mapper.readValue(content, ForumDTO.class);
        assertEquals(6L, returnedForum.getId());
    }

    @Test
    @WithMockUser
    void shouldAddTheCurrentlyAuthenticatedUsersAliasToTheReturnedForumDTO() throws Exception {
        when(mockForumRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // given that a user is authenticated
        // when a new forum is created
        String jsonForum = mapper.writeValueAsString(new ForumDTO());
        String content = mvc.perform(MockMvcRequestBuilders.post("/api/forums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForum)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        // the returned ForumDTO contains the authenticated users alias
        ForumDTO returnedForum = mapper.readValue(content, ForumDTO.class);
        assertEquals(forumUser.getAlias(), returnedForum.getCreator());
    }
}