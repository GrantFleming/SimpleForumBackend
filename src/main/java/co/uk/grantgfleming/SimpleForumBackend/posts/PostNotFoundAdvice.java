package co.uk.grantgfleming.SimpleForumBackend.posts;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class seems to be used as an exception handler within SpringMVC
 * <p>
 * I.e. if at any point in handling a request a {@link PostNotFoundException}
 * is thrown, the method is called in this class. This method simply returns a
 * 404 with whatever the message in the exception is
 */
@ControllerAdvice
public class PostNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String postNotFoundHandler(PostNotFoundException ex) {
        return ex.getMessage();
    }

}
