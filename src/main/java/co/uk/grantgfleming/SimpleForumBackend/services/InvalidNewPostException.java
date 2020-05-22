package co.uk.grantgfleming.SimpleForumBackend.services;

public class InvalidNewPostException extends RuntimeException {
    public InvalidNewPostException() {
        super("Post provided was of an incorrect format. Must have correct fields but have no id allocated.");
    }
}
