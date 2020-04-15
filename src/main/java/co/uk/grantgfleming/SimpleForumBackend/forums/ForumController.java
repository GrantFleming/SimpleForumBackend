package co.uk.grantgfleming.SimpleForumBackend.forums;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ForumController {

    private final ForumRepository repository;

    public ForumController(ForumRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/forums")
    List<Forum> allForums() {
        return repository.findAll();
    }

    @GetMapping("/forums/{id}")
    Forum one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ForumNotFoundException(id));
    }

    @PostMapping("/forums")
    @ResponseStatus(HttpStatus.CREATED)
    Forum newForum(@RequestBody Forum forum) {
        return repository.save(forum);
    }
}
