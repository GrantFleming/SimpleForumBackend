package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * An Authentication that represents authentication via the
 * "Authorization: Bearer ..." header
 * <p>
 * Useful for authenticating with JWT tokens
 */
public class BearerAuthenticationToken extends AbstractAuthenticationToken {

    public final Object principle;
    public final String credentials;

    /**
     * Creates a bearer authentication token for an authentication request.
     * <p>
     * The principle and granted authorities are set to null.
     * <p>
     * This is useful in cases where the provided token is able to both identify
     * and authenticate a user.
     *
     * @param credentials The JWT token that identifies the entity
     */
    public BearerAuthenticationToken(String credentials) {
        super(null);
        this.principle = null;
        this.credentials = credentials;
    }

    /**
     * Creates a bearer authentication token that represents an authenticated principle.
     * <p>
     * The newly created Authentication is marked as authenticated.
     *
     * @param principle          The entity that is authenticated
     * @param credentials        The bearer token used to prove the identity of the principle
     * @param grantedAuthorities The authorities granted to the authenticated principle
     */
    public BearerAuthenticationToken(Object principle, String credentials, Collection<GrantedAuthority> grantedAuthorities) {
        super(grantedAuthorities);
        this.principle = principle;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }


    /**
     * {@inheritDoc}
     * <p>
     * The credentials are the bearer token
     *
     * @return The credentials, a string containing the original bearer token
     */
    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principle;
    }
}
