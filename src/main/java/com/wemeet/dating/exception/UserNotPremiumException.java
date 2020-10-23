package com.wemeet.dating.exception;


public class UserNotPremiumException extends BadRequestException {
    public UserNotPremiumException(String message) {
        super(message);
    }

    public UserNotPremiumException() {
        super("Only Premium Users have access to this feature");
    }
}