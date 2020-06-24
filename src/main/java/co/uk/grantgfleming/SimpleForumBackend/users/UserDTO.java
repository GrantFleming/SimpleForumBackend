package co.uk.grantgfleming.SimpleForumBackend.users;

import lombok.Data;

/**
 * A data transfer object for {@link ForumUser}s
 */
@Data
public class UserDTO {

    private Long id;

    private String email;

    private String alias;

    public static UserDTO fromUser(ForumUser forumUser) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(forumUser.getId());
        userDTO.setEmail(forumUser.getEmail());
        userDTO.setAlias(forumUser.getAlias());
        return userDTO;
    }

}
