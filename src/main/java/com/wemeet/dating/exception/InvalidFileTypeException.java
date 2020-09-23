package com.wemeet.dating.exception;

public class InvalidFileTypeException extends Exception {
    public InvalidFileTypeException() {
        super("Invalid File Type.");
    }
    public InvalidFileTypeException(String message) {
        super(message);
    }

}
