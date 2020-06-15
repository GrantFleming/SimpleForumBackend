package co.uk.grantgfleming.SimpleForumBackend.security.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Date;
import java.util.Random;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * A service for generating and validating JWT tokens for security purposes.
 * <p>
 * Allows a token to be generated from a string subject or an Authentication (where it will set the JWT subject as
 * the Authentication principle name).
 * <p>
 * Validating the token returns the subject on successful validation or throws JWTVerificationException.
 */
@Service
public class JWTService {

    // is randomly generated on initialization
    private final byte[] jwtSecret;
    // for calculating an expiration time for tokens
    private final Clock clock;
    // defaults to 1 hour
    @Value("${jwt.expiration-time:3600000}")
    private long expirationTime = 3600000;

    /**
     * Creates a jwt service using the provided Random instance to generate a
     * secret for signing jwt tokens
     */
    public JWTService(Clock clock) {
        // Generate a random secret for signing JWT tokens
        // obviously this will only work as long as there is only a single instance of the
        // app working. If we ever need to spin up multiple instances behind a load balancer or
        // similar we will need to handle the secret differently
        jwtSecret = new byte[128];
        new Random().nextBytes(jwtSecret);
        this.clock = clock;
    }

    /**
     * Generates a JWT with the given subject.
     * <p>
     * Tokens generated with this method can only be verified by the same JWTService instance
     * as they are signed with a secret known only to the instance.
     * <p>
     * The expiration time may be set using the <code>jwt.expiration-time</code> property. If
     * not it defaults to an hour.
     *
     * @param subject to set as the subject of the JWT
     * @return the generated JWT
     */
    public String generateJWT(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withExpiresAt(new Date(clock.millis() + expirationTime))
                .sign(HMAC512(jwtSecret));
    }

    /**
     * Generates a JWT from the given Authentication.
     * <p>
     * Sets the subject of the JWT to the Authentication name.
     * <p>
     * Tokens generated with this method can only be verified by the same JWTService instance
     * as they are signed with a secret known only to the instance.
     * <p>
     * The expiration time may be set using the <code>jwt.expiration-time</code> property. If
     * not it defaults to an hour.
     *
     * @param authentication from which to generate a jwt
     * @return the generated jwt
     */
    public String generateJWT(Authentication authentication) {
        return JWT.create()
                .withSubject(authentication.getName())
                .withExpiresAt(new Date(clock.millis() + expirationTime))
                .sign(HMAC512(jwtSecret));
    }

    /**
     * Verifies a given JWT and if it is valid and was signed with the secret known
     * only to this instance, the subject is returned.
     *
     * @param token to be verified
     * @return the subject of the token on successful verification
     * @throws JWTVerificationException if the token verification fails. If required, more information
     *                                  can be gleaned by querying the runtime type of the thrown exception.
     */
    public String validateJWT(String token) throws JWTVerificationException {
        return JWT.require(HMAC512(jwtSecret)).build().verify(token).getSubject();
    }

}
