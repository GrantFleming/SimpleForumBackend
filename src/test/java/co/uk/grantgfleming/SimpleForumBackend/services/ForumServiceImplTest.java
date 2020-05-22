package co.uk.grantgfleming.SimpleForumBackend.services;

import co.uk.grantgfleming.SimpleForumBackend.data_access.ForumRepository;

/**
 * Unit test {@link ForumServiceImpl} by overriding the interface test and
 * providing a concrete instance for the test
 */
class ForumServiceImplTest extends ForumServiceTest {

    @Override
    public ForumService createInstance(ForumRepository repository) {
        return new ForumServiceImpl(repository);
    }
}