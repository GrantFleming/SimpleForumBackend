package co.uk.grantgfleming.SimpleForumBackend.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    public User getUserByEmail(String email) throws UserNotFoundException {
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Convenience method as usernames are also emails in our user system.
     * <p>
     * Delegates to {@link #getUserByEmail}
     */
    public User getUser(String username) throws UserNotFoundException {
        return getUserByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = getUserByEmail(username);
        } catch (UserNotFoundException e) {
            // Translate our internal exception to the Spring security specific one
            throw new UsernameNotFoundException(e.getMessage());
        }
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(authority));
    }
}
