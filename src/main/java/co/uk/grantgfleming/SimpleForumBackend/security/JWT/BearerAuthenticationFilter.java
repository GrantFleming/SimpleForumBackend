package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Attempts authentication of requests that provide a 'Bearer' token.
 */
@Slf4j
public class BearerAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * Creates a BearerAuthenticationFilter that attempts to authenticate requests to the
     * endpoints identified by the given string (which can contain ant patterns).
     *
     * @param defaultFilterProcessesUrl describing the endpoints for which this filter will attempt authentication
     */
    public BearerAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    /**
     * Attempts authentication of the given request.
     *
     * @param request  the http request
     * @param response the http response
     * @return An authentication representing a successfully authenticated entity
     * @throws AuthenticationException If authentication fails
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication authRequest = new BearerAuthenticationToken(extractBearerToken(request));
        return getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation the request only matches if it also contains a non-empty bearer token.
     *
     * @param request  the http request
     * @param response the http response
     * @return <code>true</code> if authentication is required <code>false</code> otherwise.
     */
    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!super.requiresAuthentication(request, response))
            return false;

        boolean shouldAuthenticate = !extractBearerToken(request).isEmpty();

        if (shouldAuthenticate)
            log.debug("Attempting authentication with Bearer token.");

        return shouldAuthenticate;
    }

    /**
     * Attempts to extract a bearer token from the given request
     *
     * @param request from which the bearer token should be extracted
     * @return the bearer token extracted from the request or the empty string if
     * no such token exists in the request
     */
    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return "";

        // Remove "Bearer "
        return authHeader.substring(7);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method extends the functionality of that in {@link AbstractAuthenticationProcessingFilter} by continuing
     * down the filter chain after authentication has taken place rather than redirecting.
     * <p>
     * As bearer tokens are often provided as authentication/authorization mechanisms in the header of the request
     * for the resource itself this allows the authentication to take place and then pass along the request to be
     * processed by the system.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
