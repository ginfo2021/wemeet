package com.wemeet.dating.exception;


public class BlockedUserException extends BadRequestException {
    public BlockedUserException(String message) {
        super(message);
    }

    public BlockedUserException() {
        super("You have been blocked by this User");
    }
}