package ru.thuggeelya.useraccounts.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("Invalid login or password");
    }

    public AuthenticationException(final String message) {
        super(message);
    }
}
