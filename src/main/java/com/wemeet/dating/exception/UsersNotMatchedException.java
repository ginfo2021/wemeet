package com.wemeet.dating.exception;

public class UsersNotMatchedException extends BadRequestException {
    public UsersNotMatchedException(String message) {
        super(message);
    }

    public UsersNotMatchedException() {
        super("Users passed are not Matched");
    }
}
