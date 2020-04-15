package co.uk.grantgfleming.SimpleForumBackend.posts;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostRepository repository;

    public PostController(PostRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/posts")
    List<Post> all() {
        return repository.findAll();
    }

    @GetMapping("/posts/{id}")
    Post one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @PostMapping("/posts")
    Post newPost(@RequestBody Post post) {
        return repository.save(post);
    }

    @PutMapping("/employees/{id}")
    Post replaceEmployee(@RequestBody Post newPost, @PathVariable Long id) {
        return repository.findById(id).map(post -> {
            post.setTitle(newPost.getTitle());
            post.setBody(newPost.getBody());
            return repository.save(post);
        }).orElseGet(() -> {
            newPost.setId(id);
            return repository.save(newPost);
        });
    }

    @DeleteMapping("/posts/{id}")
    void deletePost(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
