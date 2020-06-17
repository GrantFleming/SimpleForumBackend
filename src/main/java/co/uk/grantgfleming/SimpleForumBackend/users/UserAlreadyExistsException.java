package co.uk.grantgfleming.SimpleForumBackend.users;

public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(String identifier) {
        super("An account already exists for " + identifier);
    }

}
