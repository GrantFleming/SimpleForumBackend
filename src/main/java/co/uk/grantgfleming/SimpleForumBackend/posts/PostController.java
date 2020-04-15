package co.uk.grantgfleming.SimpleForumBackend.posts;

import co.uk.grantgfleming.SimpleForumBackend.forums.ForumRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostRepository repository;
    private final ForumRepository forumRepository;

    public PostController(PostRepository repository, ForumRepository forumRepository) {
        this.repository = repository;
        this.forumRepository = forumRepository;
    }

    @GetMapping("/posts")
    List<Post> byForumId(@RequestParam("forumId") Long forumId) {
        return repository.findByForumId(forumId);
    }

    @GetMapping("/posts/{id}")
    Post one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    Post newPost(@RequestBody Post post) {
        if (forumRepository.existsById(post.getForumId())) {
            return repository.save(post);
        } else {
            return post;
        }
    }

    @DeleteMapping("/posts/{id}")
    void deletePost(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
