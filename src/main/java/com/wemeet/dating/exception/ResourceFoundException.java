package com.wemeet.dating.exception;


public class ResourceFoundException extends Exception {


    public ResourceFoundException(String message) {
        super(message);
    }

    public ResourceFoundException() {
        super("Resource not Found");
    }

}

