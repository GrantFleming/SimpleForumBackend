package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import co.uk.grantgfleming.SimpleForumBackend.users.ForumUser;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * For authenticating BearerAuthenticationTokens where the credentials are a JWT token
 * specifying the username of a user to be authenticated.
 * <p>
 * It attempts to get a {@link ForumUser} from the {@link UserService} with the username provided
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
        UserDetails user;
        try {
            String username = jwtService.validateJWT(bearerToken);
            user = userService.loadUserByUsername(username);
        } catch (JWTVerificationException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (UsernameNotFoundException e) {
            throw new ProviderNotFoundException(e.getMessage());
        }

        return new BearerAuthenticationToken(user, bearerToken, user.getAuthorities());
    }

}
