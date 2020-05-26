package co.uk.grantgfleming.SimpleForumBackend.forum.services;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.ForumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

abstract class ForumServiceTest {

    private ForumService sut;
    private ForumRepository mockRepo;

    public abstract ForumService createInstance(ForumRepository repository);

    @BeforeEach
    void setUp() {
        mockRepo = mock(ForumRepository.class);
        sut = createInstance(mockRepo);
    }

    @Test
    void shouldReturnEverythingInTheRepo() {
        // given there are Forums in the repo
        Forum[] forums = {new Forum(), new Forum(), new Forum()}; // three forums in repo
        when(mockRepo.findAll()).thenReturn(Arrays.asList(forums));

        // when all Forums are requested
        List<Forum> returnedForums = sut.allForums();

        // then all the forums in the repo should be returned
        assertArrayEquals(forums, returnedForums.toArray());
    }

    @Test
    void shouldReturnEmptyListIfNothingInRepo() {
        // given there are no Forums in the repo
        when(mockRepo.findAll()).thenReturn(Collections.emptyList());

        // when all Forums are requested
        List<Forum> returnedForums = sut.allForums();

        // an empty list should be returned
        assertTrue(returnedForums.isEmpty());
    }

    @Test
    void shouldReturnAForumFromIdIfItExists() {
        // given a Forum exists with a certain id
        Forum testForum = new Forum();
        testForum.setId(3L);
        when(mockRepo.findById(3L)).thenReturn(of(testForum));

        // when that forum is request by id
        Forum returnedForum = sut.findForumById(3L);

        // then the Forum should be returned
        assertEquals(testForum, returnedForum);
    }

    @Test
    void shouldThrowIfForumIsNotFound() {
        // given no Forum exists with a certain id
        when(mockRepo.findById(3L)).thenReturn(empty());

        // when a Forum is request with that id
        // then a ForumNotFoundException should be thrown
        assertThrows(ForumNotFoundException.class, () -> sut.findForumById(3L));
    }

    @Test
    void shouldAddAForumWithNoId() {
        // given that a forum does not contain an Id
        Forum testForum = new Forum();
        Forum repoCreatedForum = new Forum();
        when(mockRepo.save(testForum)).thenReturn(repoCreatedForum);

        // when it is added through the service
        Forum returnedForum = sut.addForum(testForum);

        // then it is added to the underlying repository
        verify(mockRepo, times(1)).save(testForum);

        // and whatever is given back from the repository is returned
        assertEquals(repoCreatedForum, returnedForum);
    }

    @Test
    void shouldThrowIfForumContainsId() {
        // given that a forum already has an id
        Forum forum = new Forum();
        forum.setId(3L);

        // when it is added through the service
        // then an InvalidNewForumException is thrown
        assertThrows(InvalidNewForumException.class, () -> sut.addForum(forum));

        // and it is not added ot the repository
        verify(mockRepo, never()).save(any());
    }

    @Test
    void shouldDetectExistenceOfAForumAccordingToUnderlyingStore() {
        when(mockRepo.existsById(anyLong())).thenReturn(false);
        when(mockRepo.existsById(2L)).thenReturn(true);
        assertFalse(sut.existsById(1L));
        assertTrue(sut.existsById(2L));
    }
}