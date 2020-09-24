package com.wemeet.dating.model.enums;

public enum MessageType {
    TEXT("TEXT"),
    MEDIA("MEDIA");

    private final String name;

    MessageType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

