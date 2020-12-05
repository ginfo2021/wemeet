package com.wemeet.dating.exception;

public class BadRequestException extends Exception {

    public BadRequestException() {
        super("Ensure all fields are parsed appropriately");
    }

    public BadRequestException(String message) {
        super(message);
    }
}
