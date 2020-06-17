package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.ForumRepository;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;

/**
 * Unit test {@link ForumServiceImpl} by overriding the interface test and
 * providing a concrete instance for the test
 */
class ForumServiceImplTest extends ForumServiceTest {

    @Override
    public ForumService createInstance(ForumRepository repository, UserService userService) {
        return new ForumServiceImpl(repository, userService);
    }
}