package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.Post;
import co.uk.grantgfleming.SimpleForumBackend.forum.PostDTO;
import co.uk.grantgfleming.SimpleForumBackend.forum.PostRepository;
import co.uk.grantgfleming.SimpleForumBackend.users.User;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The default implementation of {@link PostService}
 *
 * @see PostService
 */
@Service
public class PostServiceImpl implements PostService {

    /**
     * The underlying data store from which this service serves
     */
    private final PostRepository repository;
    private final ForumService forumService;
    private final UserService userService;

    /**
     * Creates a PostServiceImpl
     *
     * @param repository   to use as the underlying data store
     * @param forumService to check existence of ForumIds
     * @param userService  to get the currently authenticated user
     */
    public PostServiceImpl(PostRepository repository, ForumService forumService, UserService userService) {
        this.repository = repository;
        this.forumService = forumService;
        this.userService = userService;
    }

    @Override
    public List<Post> getPostsForForum(Long forumId) throws ForumNotFoundException {
        if (!forumService.existsById(forumId)) {
            throw new ForumNotFoundException(forumId);
        }

        return repository.findByForumId(forumId);
    }

    @Override
    public Post findPostById(Long id) throws PostNotFoundException {
        return repository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Override
    public Post addPost(PostDTO postDTO) throws InvalidNewPostException, ForumNotFoundException {
        Forum forum = forumService.findForumById(postDTO.getForumId());
        User creator = userService.getCurrentAuthenticatedUser();

        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setBody(postDTO.getBody());
        post.setForum(forum);
        post.setCreator(creator);

        post = repository.save(post);
        return post;
    }
}
