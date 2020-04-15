package co.uk.grantgfleming.SimpleForumBackend.forums;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Forum {

    private @Id
    @GeneratedValue(generator = "FORUM_GENERATOR")
    Long id;
    private String name;
    private String description;

    public Forum() {
    }

    public Forum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
