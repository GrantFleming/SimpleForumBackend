package co.uk.grantgfleming.SimpleForumBackend.forum;

import co.uk.grantgfleming.SimpleForumBackend.users.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

/**
 * Represents a forum
 */
@Data
@Entity
public class Forum {

    private @Id
    @GeneratedValue(generator = "FORUM_GENERATOR")
    Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.REMOVE)
    private Collection<Post> posts;

    @ManyToOne
    private User user;
}

