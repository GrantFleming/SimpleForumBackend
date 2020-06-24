package co.uk.grantgfleming.SimpleForumBackend.security;

import co.uk.grantgfleming.SimpleForumBackend.security.JWT.JWTService;
import co.uk.grantgfleming.SimpleForumBackend.users.ForumUser;
import co.uk.grantgfleming.SimpleForumBackend.users.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * A convenient base class for testing how the security chain behaves to requests by certain
 * types of users.
 * <p>
 * Allows subclasses to build requests that are performed as a certain type of user.
 * <p>
 * When such requests are performed the user type is created in the repository, the request
 * is then performed providing credentials to authenticate as this newly created user, and afterwards
 * the user is removed again.
 * <p>
 * Useful to remove lots of setup code when testing security chain behaviour.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class SecurityChainIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JWTService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper = new ObjectMapper();

    public RequestBuilder request() {
        return new RequestBuilder();
    }

    /**
     * For building security requests to endpoint for certain types of user.
     * <p>
     * For example to formulate a POST request for an authenticated user with the role
     * of "USER" to "/banana":
     * <p>
     * request
     * .endpoint("/banana")
     * .method(HttpMethod.POST)
     * .authenticated()
     * .withRole("USER")
     * .perform();
     * <p>
     * This creates the required type of user in the repository, builds the request authenticating
     * as the newly created user without shortcutting any of the security chain, and returns the result
     * of performing the request.
     * <p>
     * This allows us to test how the security chain behaves to certain user types making certain requests
     * to certain endpoints.
     */
    public class RequestBuilder {

        private String endpoint = "";
        private HttpMethod method = HttpMethod.GET;
        private boolean authenticatedUserRequired = false;
        private boolean basicAuthenticationRequired = false;
        private ForumUser.Role role = ForumUser.Role.NONE;
        private String content = "{}";
        private MediaType contentType = MediaType.APPLICATION_JSON;

        public RequestBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public RequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public RequestBuilder authenticated() {
            authenticatedUserRequired = true;
            return this;
        }

        public RequestBuilder withBasic() {
            basicAuthenticationRequired = true;
            return this;
        }

        public RequestBuilder withRole(ForumUser.Role role) {
            this.role = role;
            return this;
        }

        public RequestBuilder contentType(MediaType contentType) {
            this.contentType = contentType;
            return this;
        }

        public RequestBuilder withBodyAsJson(Object content) throws JsonProcessingException {
            this.content = objectMapper.writeValueAsString(content);
            return this;
        }

        public ResultActions perform() throws Exception {
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(method, endpoint);

            ForumUser newForumUser = null;
            if (authenticatedUserRequired) {
                // Create a new user with the required roles;
                newForumUser = createNewUserInRepository(role);
                addAuthorizationHeader(request, newForumUser);
            }

            request.content(content);
            request.contentType(contentType);

            // perform the request itself
            ResultActions result = mvc.perform(request);
            // remove the user we created to perform the request
            if (newForumUser != null)
                userRepository.delete(newForumUser);

            return result;
        }

        private void addAuthorizationHeader(MockHttpServletRequestBuilder request, ForumUser newForumUser) {
            if (basicAuthenticationRequired) {
                String userPassString = newForumUser.getEmail() + ":password";
                String basicAuth = Base64.toBase64String(userPassString.getBytes());
                request.header("Authorization", "Basic " + basicAuth);
            } else {
                // Create the request with a valid JWT token then authenticates the newly created user
                String jwtToken = jwtService.generateJWT(newForumUser.getEmail());
                request.header("Authorization", "Bearer " + jwtToken);
            }
        }

        private ForumUser createNewUserInRepository(ForumUser.Role role) {
            ForumUser forumUser = new ForumUser();
            forumUser.setEmail("user@email.com");
            forumUser.setAlias("someAlias");
            forumUser.setPassword(passwordEncoder.encode("password"));
            forumUser.setRole(role);
            return userRepository.save(forumUser);
        }

    }
}
