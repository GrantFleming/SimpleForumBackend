package co.uk.grantgfleming.SimpleForumBackend.controllers;

import co.uk.grantgfleming.SimpleForumBackend.data_access.Post;
import co.uk.grantgfleming.SimpleForumBackend.services.ForumNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.services.InvalidNewPostException;
import co.uk.grantgfleming.SimpleForumBackend.services.PostNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    List<Post> getPostsForForum(@RequestParam("forumId") Long forumId) {
        return postService.getPostsForForum(forumId);
    }

    @GetMapping("{id}")
    Post getPostById(@PathVariable Long id) {
        return postService.findPostById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    Post postNewPost(@RequestBody Post post) {
        return postService.addPost(post);
    }

    /*
        Exception handlers
     */

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String postNotFoundHandler(PostNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidNewPostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String invalidNewPostHandler(InvalidNewPostException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ForumNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String forumNotFoundHandler(ForumNotFoundException ex) {
        return ex.getMessage();
    }
}
