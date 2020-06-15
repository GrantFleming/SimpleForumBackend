package co.uk.grantgfleming.SimpleForumBackend.users;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(String username) {
        super("An account already exists for: " + username);
    }

}
