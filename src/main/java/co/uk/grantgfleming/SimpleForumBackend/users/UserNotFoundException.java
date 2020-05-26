package co.uk.grantgfleming.SimpleForumBackend.users;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String username) {
        super("No user found for: " + username);
    }

}
