package co.uk.grantgfleming.SimpleForumBackend.users;

import co.uk.grantgfleming.SimpleForumBackend.forum.Forum;
import co.uk.grantgfleming.SimpleForumBackend.forum.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ForumUser {

    @Id
    @GeneratedValue(generator = "USER_GENERATOR")
    private Long id;

    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;

    @NotEmpty
    @Column(unique = true)
    private String alias;

    @JsonIgnore
    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {NONE, USER}

    /**
     * The following attributes are excluded from toString and hashcode
     * generation as they are lazily loaded from the database:
     */

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE)
    private Set<Post> posts = new HashSet<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "forumUser", cascade = CascadeType.REMOVE)
    private Set<Forum> forums = new HashSet<>();
}