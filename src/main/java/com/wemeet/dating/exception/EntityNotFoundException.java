package com.wemeet.dating.exception;


public class EntityNotFoundException extends Exception {


    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException() {
        super("Entity not Found");
    }

}


