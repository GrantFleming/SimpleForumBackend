package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.users.ForumUser;
import lombok.Data;

import javax.persistence.*;

/**
 * Represents a post
 */
@Data
@Entity
public class Post {

    private @Id
    @GeneratedValue(generator = "POST_GENERATOR")
    Long id;

    @ManyToOne
    private Forum forum;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ForumUser creator;

    private String title;

    private String body;

}
