package co.uk.grantgfleming.SimpleForumBackend.forum;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

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
class ForumControllerIT {

    @MockBean
    ForumRepository mockForumRepository;
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser
    void shouldGetAllForums() throws Exception {
        // given there are three forums in the repository
        Forum[] testForums = {new Forum(), new Forum(), new Forum()};
        String testForumsAsJson = mapper.writeValueAsString(testForums);
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
        String forumAsJson = mapper.writeValueAsString(forum);
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
        when(mockForumRepository.save(any())).thenReturn(forum);

        // when a post request is make to api/forums with a valid forum in json format
        // (valid here means correct format but with no set id)
        //then a 201 is returned along with the forum object with the id field generated
        String jsonForum = mapper.writeValueAsString(new Forum());
        String content = mvc.perform(MockMvcRequestBuilders.post("/api/forums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForum)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        Forum returnedForum = mapper.readValue(content, Forum.class);
        assertEquals(6L, returnedForum.getId());
    }

    @Test
    @WithMockUser
    void shouldReturn400IfSuppliedForumHasAnId() throws Exception {
        // when a post request is made where the forum json object has an id
        // then a 400 is returned and it isn't added to the repository

        Forum forum = new Forum();
        forum.setId(1L);
        String jsonForum = mapper.writeValueAsString(forum);
        mvc.perform(MockMvcRequestBuilders.post("/api/forums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForum)).andExpect(status().isBadRequest());

        verify(mockForumRepository, never()).save(any());
    }
}