package com.wemeet.dating.model.enums;

public enum MessageStatus {
    DELIVERED("DELIVERED"),
    READ("READ");

    private final String name;

    MessageStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
