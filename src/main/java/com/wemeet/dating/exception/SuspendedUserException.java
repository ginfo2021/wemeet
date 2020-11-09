package com.wemeet.dating.exception;

public class SuspendedUserException extends Exception {

    public SuspendedUserException() {
        super("You have been Suspended");
    }

    public SuspendedUserException(String message) {
        super(message);
    }
}
