package com.wemeet.dating.exception;

public class PreferenceNotSetException extends BadRequestException {
    public PreferenceNotSetException(String message) {
        super(message);
    }

    public PreferenceNotSetException() {
        super("User has not set preferences");
    }
}
