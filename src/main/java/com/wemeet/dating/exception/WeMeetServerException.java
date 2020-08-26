package com.wemeet.dating.exception;

public class WeMeetServerException extends RuntimeException {

    public WeMeetServerException() {
    }

    public WeMeetServerException(String message) {
        super(message);
    }
}
