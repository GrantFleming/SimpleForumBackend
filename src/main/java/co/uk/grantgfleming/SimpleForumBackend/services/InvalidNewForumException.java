package co.uk.grantgfleming.SimpleForumBackend.services;

public class InvalidNewForumException extends RuntimeException {
    public InvalidNewForumException() {
        super("Forum provided was of an incorrect format. Must have correct field but have no id allocated.");
    }
}
