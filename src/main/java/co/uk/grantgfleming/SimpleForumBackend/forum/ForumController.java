package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumService;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewForumException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("")
    List<Forum> getAllForums() {
        return forumService.allForums();
    }

    @GetMapping("/{id}")
    Forum getForumById(@PathVariable Long id) {
        return forumService.findForumById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Forum postNewForum(@RequestBody Forum forum) {
        return forumService.addForum(forum);
    }

    @ExceptionHandler(InvalidNewForumException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidForumHandler(InvalidNewForumException ex) {
        return ex.getMessage();
    }

}
