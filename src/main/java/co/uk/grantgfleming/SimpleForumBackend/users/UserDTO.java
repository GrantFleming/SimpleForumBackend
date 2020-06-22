package co.uk.grantgfleming.SimpleForumBackend.users;

import lombok.Data;

/**
 * A data transfer object for {@link User}s
 */
@Data
public class UserDTO {

    private Long id;

    private String email;

    private String alias;

    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setAlias(user.getAlias());
        return userDTO;
    }

}
