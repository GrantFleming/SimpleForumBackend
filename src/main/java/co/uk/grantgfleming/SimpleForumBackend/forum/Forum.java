package co.uk.grantgfleming.SimpleForumBackend.forum;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

}

