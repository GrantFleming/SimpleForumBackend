package co.uk.grantgfleming.SimpleForumBackend.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Contains endpoints for user account information and manipulation
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO register(@RequestParam String email, @RequestParam String alias, @RequestParam String password) throws UserAlreadyExistsException {
        if (emailExists(email))
            throw new UserAlreadyExistsException("email: " + email);
        else if (repository.existsByAlias(alias))
            throw new UserAlreadyExistsException("alias: " + alias);

        ForumUser forumUser = new ForumUser();
        forumUser.setEmail(email);
        forumUser.setAlias(alias);
        forumUser.setPassword(passwordEncoder.encode(password));
        forumUser.setRole(ForumUser.Role.USER);

        ForumUser newForumUser = repository.save(forumUser);
        return UserDTO.fromUser(newForumUser);
    }

    @GetMapping("/validateEmail")
    Boolean emailExists(@RequestParam String email) {
        return repository.existsByEmail(email);
    }

    @GetMapping("/validateAlias")
    Boolean aliasExists(@RequestParam String alias) {
        return repository.existsByAlias(alias);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String userAlreadyExistsHandler(UserAlreadyExistsException e) {
        return e.getMessage();
    }

}