package co.uk.grantgfleming.SimpleForumBackend.controllers;

import co.uk.grantgfleming.SimpleForumBackend.data_access.Forum;
import co.uk.grantgfleming.SimpleForumBackend.services.ForumService;
import co.uk.grantgfleming.SimpleForumBackend.services.InvalidNewForumException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forums")
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
