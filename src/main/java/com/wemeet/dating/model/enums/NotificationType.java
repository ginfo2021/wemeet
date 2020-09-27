package com.wemeet.dating.model.enums;

public enum NotificationType {
    PUSH("push");

    private final String name;

    NotificationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
