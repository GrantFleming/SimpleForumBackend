package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumService;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewForumException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

    private final ForumService forumService;

    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("")
    List<ForumDTO> getAllForums() {
        return forumService.allForums().stream().map(ForumDTO::fromForum).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    ForumDTO getForumById(@PathVariable Long id) {
        Forum forum = forumService.findForumById(id);
        return ForumDTO.fromForum(forum);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    ForumDTO postNewForum(@RequestBody ForumDTO forumDTO) {
        Forum forum = forumService.addForum(forumDTO);
        return ForumDTO.fromForum(forum);
    }

    @ExceptionHandler(InvalidNewForumException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidForumHandler(InvalidNewForumException ex) {
        return ex.getMessage();
    }

}
