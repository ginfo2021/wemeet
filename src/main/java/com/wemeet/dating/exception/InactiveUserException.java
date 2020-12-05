package com.wemeet.dating.exception;

public class InactiveUserException extends Exception {

    public InactiveUserException() {
        super("Current User is not active");
    }

    public InactiveUserException(String message) {
        super(message);
    }
}
