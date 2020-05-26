package co.uk.grantgfleming.SimpleForumBackend.forum.services;

public class InvalidNewForumException extends RuntimeException {
    public InvalidNewForumException() {
        super("Forum provided was of an incorrect format. Must have correct fields but have no id allocated.");
    }
}
