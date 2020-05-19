package co.uk.grantgfleming.SimpleForumBackend.services;

import co.uk.grantgfleming.SimpleForumBackend.data_access.PostRepository;

/**
 * Unit test {@link PostServiceImpl} by extending the interface test
 * and providing the {@link PostService} as a concrete instance
 */
class PostServiceImplTest extends PostServiceTest {

    @Override
    public PostService createInstance(PostRepository repository, ForumService forumService) {
        return new PostServiceImpl(repository, forumService);
    }

}