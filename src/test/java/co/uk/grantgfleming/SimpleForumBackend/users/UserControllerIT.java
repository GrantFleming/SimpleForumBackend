package co.uk.grantgfleming.SimpleForumBackend.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the /user/* endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserRepository userRepository;
    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    void shouldReturn201OnSuccessfulRegistration() throws Exception {
        when(userRepository.save(any())).thenReturn(new User());
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=someemail&alias=somealias&password=somepassword")).andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400WhenAttemptingToRegisterExistingEmail() throws Exception {
        String email = "someemail";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=" + email + "&password=somepassword")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAttemptingToRegisterExistingAlias() throws Exception {
        String alias = "somealias";

        when(userRepository.existsByAlias(alias)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=someemail&" + alias + "&password=somepassword")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAttemptingRegistrationWithMissingEmail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("alias=somealias&password=somepassword")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAttemptingRegistrationWithMissingAlias() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=someemail&password=somepassword")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAttemptingRegistrationWithMissingPassword() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=someemail&alias=somealias")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenValidatingEmailIfAnEmailIsProvided() throws Exception {
        // whither or not the email exists status should always be 200
        when(userRepository.existsByEmail(any())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.get("/user/validateEmail?email=someemail")).andExpect(status().isOk());

        when(userRepository.existsByEmail(any())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.get("/user/validateEmail?email=someemail")).andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenValidatingEmailIfNoEmailIsProvided() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/validateEmail")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200WhenValidatingAliasIfAnAliasIsProvided() throws Exception {
        // whither or not the alias exists the status should always be 200
        when(userRepository.existsByAlias(any())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.get("/user/validateAlias?alias=somealias"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));

        when(userRepository.existsByAlias(any())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.get("/user/validateAlias?alias=somealias"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    @Test
    void shouldReturn400WhenValidatingAliasIfNoAliasIsProvided() throws Exception {
        // whither or not the alias exists the status should always be 400
        when(userRepository.existsByAlias(any())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.get("/user/validateAlias"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(emptyString()));

        when(userRepository.existsByAlias(any())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.get("/user/validateAlias"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(emptyString()));
    }
}
