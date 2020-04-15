package co.uk.grantgfleming.SimpleForumBackend.posts;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Post {

    private @Id
    @GeneratedValue(generator = "POST_GENERATOR")
    Long id;
    private Long forumId;
    private String title;
    private String body;

    public Post() {
    }

    public Post(Long forumId, String title, String body) {
        this.forumId = forumId;
        this.title = title;
        this.body = body;
    }
}
