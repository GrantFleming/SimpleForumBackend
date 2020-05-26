package co.uk.grantgfleming.SimpleForumBackend.forum;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a post
 */
@Data
@Entity
public class Post {

    private @Id
    @GeneratedValue(generator = "POST_GENERATOR")
    Long id;
    private Long forumId;
    private String title;
    private String body;

}
