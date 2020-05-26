package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import co.uk.grantgfleming.SimpleForumBackend.users.User;
import co.uk.grantgfleming.SimpleForumBackend.users.UserNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * For authenticating BearerAuthenticationTokens where the credentials are a JWT token
 * specifying the username of a user to be authenticated.
 * <p>
 * It attempts to get a {@link User} from the {@link UserService} with the username provided
 * by the token.
 */
@Slf4j
@Component("jwtAuthenticationManager")
public class JWTAuthenticationManager implements AuthenticationManager {

    private final JWTService jwtService;
    private final UserService userService;

    public JWTAuthenticationManager(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof BearerAuthenticationToken)) {
            log.error("JWTAuthenticationManager called to authenticate a non-BearerAuthenticationToken");
            throw new IllegalArgumentException("JWTAuthenticationManager can only authenticate " + "BearerAuthenticationTokens");
        }

        String bearerToken = (String) authentication.getCredentials();
        User user;
        try {
            String username = jwtService.validateJWT(bearerToken);
            user = userService.getUser(username);
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new ProviderNotFoundException(e.getMessage());
        }

        return buildAuthentication(user, bearerToken);
    }


    private Authentication buildAuthentication(User user, String jwtToken) {
        String role = "NONE";
        if (user.getRole() != null)
            role = "ROLE_" + user.getRole().name();
        GrantedAuthority userRole = new SimpleGrantedAuthority(role);

        return new BearerAuthenticationToken(user, jwtToken, Collections.singleton(userRole));
    }

}
