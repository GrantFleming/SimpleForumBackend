package co.uk.grantgfleming.SimpleForumBackend.forums;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ForumNotFoundException extends RuntimeException {
    public ForumNotFoundException(Long id) {
        super("Could not find forum with id: " + id);
    }
}
