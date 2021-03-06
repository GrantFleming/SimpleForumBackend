package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.PostRepository;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;

/**
 * Unit test {@link PostServiceImpl} by extending the interface test
 * and providing the {@link PostService} as a concrete instance
 */
class PostServiceImplTest extends PostServiceTest {

    @Override
    public PostService createInstance(PostRepository repository, ForumService forumService, UserService userService) {
        return new PostServiceImpl(repository, forumService, userService);
    }

}