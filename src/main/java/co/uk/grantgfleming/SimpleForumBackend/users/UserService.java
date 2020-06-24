package co.uk.grantgfleming.SimpleForumBackend.users;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * Gets the user with the given email
     *
     * @param email with which to find the user
     * @return the user with the given email
     * @throws UserNotFoundException if no user exists with the given email
     */
    public ForumUser getUserByEmail(String email) throws UserNotFoundException {
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Convenience method as usernames are also emails in our user system.
     * <p>
     * Delegates to {@link #getUserByEmail}
     */
    public ForumUser getUser(String username) throws UserNotFoundException {
        return getUserByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser forumUser;
        try {
            forumUser = getUserByEmail(username);
        } catch (UserNotFoundException e) {
            // Translate our internal exception to the Spring security specific one
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + forumUser.getRole().name());
        return new org.springframework.security.core.userdetails.User(forumUser.getEmail(), forumUser.getPassword(), Collections
                .singletonList(authority));
    }

    /**
     * Gets the currently authenticated user.
     */
    @Secured("ROLE_USER")
    public ForumUser getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principle = (UserDetails) authentication.getPrincipal();
        ForumUser authenticatedForumUser;
        try {
            authenticatedForumUser = getUserByEmail(principle.getUsername());
            return authenticatedForumUser;
        } catch (UserNotFoundException e) {
            /*
            This should never throw, the UserDetails object that is the currently authenticated
            principle is generated from a User object, so it should always be possible to get
            the User object for the given UserDetails principle except in the most extreme
            of circumstances:

            i.e. if two users log into the same account independently, one user deleted the
            the account while the user's request is being processed then it is possible that
            it throws here.
             */
            e.printStackTrace();
        }
        return null;
    }
}
