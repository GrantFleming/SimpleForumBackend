package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.ForumDTO;

import java.util.List;

/**
 * A service that maintains a list of Forums.
 * <p>
 * The service facilitates access to and operations on the underlying
 * store of forums.
 */
public interface ForumService {

    /**
     * Get all {@link Forum}s known to the service
     *
     * @return a list of all Forums known to the service
     */
    List<Forum> allForums();

    /**
     * Returns the {@link Forum} with the given ID.
     *
     * @param id of the Forum being requested
     * @return the Forum with the given id
     * @throws ForumNotFoundException if no Forum exists with the given ID.
     */
    Forum findForumById(Long id) throws ForumNotFoundException;

    /**
     * Adds the given  {@link Forum} to the underlying store.
     *
     * @param forum to be added to the store
     * @return the forum (now with a generated id) that has been successfully added to the store
     * @throws InvalidNewForumException if the provided forum already has an id set, this key should be
     *                                  auto-generated by the store to avoid conflicts.
     */
    Forum addForum(ForumDTO forum) throws InvalidNewForumException;

    /**
     * Returns true if the underlying store contains a Forum with the given id
     *
     * @param id of the Forum for which existence is being checked
     * @return true if the Forum with the given id exists
     */
    boolean existsById(Long id);

}
