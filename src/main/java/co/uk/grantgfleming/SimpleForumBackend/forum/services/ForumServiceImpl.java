package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.ForumDTO;
import co.uk.grantgfleming.SimpleForumBackend.forum.ForumRepository;
import co.uk.grantgfleming.SimpleForumBackend.users.User;
import co.uk.grantgfleming.SimpleForumBackend.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The default implementation of {@link ForumService}
 * <p>
 *
 * @see ForumService
 */
@RequiredArgsConstructor
@Service
public class ForumServiceImpl implements ForumService {

    /**
     * The underlying data store from which this service serves
     */
    private final ForumRepository repository;
    private final UserService userService;

    @Override
    public List<Forum> allForums() {
        return repository.findAll();
    }

    @Override
    public Forum findForumById(Long id) throws ForumNotFoundException {
        return repository.findById(id).orElseThrow(() -> new ForumNotFoundException(id));
    }

    @Override
    public Forum addForum(ForumDTO forumDTO) throws InvalidNewForumException {
        User creator = userService.getCurrentAuthenticatedUser();

        Forum forum = new Forum();
        forum.setName(forumDTO.getName());
        forum.setDescription(forumDTO.getDescription());
        forum.setUser(creator);

        return repository.save(forum);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
