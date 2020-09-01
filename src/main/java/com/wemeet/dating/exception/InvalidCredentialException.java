package com.wemeet.dating.exception;


public class InvalidCredentialException extends Exception {

    public InvalidCredentialException() {
        super("Incorrect email or password");
    }

    public InvalidCredentialException(String message) {
        super(message);
    }
}
