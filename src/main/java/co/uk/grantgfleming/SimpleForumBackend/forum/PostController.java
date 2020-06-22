package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.forum.services.ForumNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.InvalidNewPostException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.PostNotFoundException;
import co.uk.grantgfleming.SimpleForumBackend.forum.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The controller for accessing Posts
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    List<PostDTO> getPostsForForum(@RequestParam("forumId") Long forumId) {
        List<Post> posts = postService.getPostsForForum(forumId);
        return posts.stream().map(PostDTO::fromPost).collect(Collectors.toList());
    }

    @GetMapping("{id}")
    PostDTO getPostById(@PathVariable Long id) {
        Post post = postService.findPostById(id);
        return PostDTO.fromPost(post);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    PostDTO postNewPost(@RequestBody PostDTO postDTO) {
        Post createdPost = postService.addPost(postDTO);
        return PostDTO.fromPost(createdPost);
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
