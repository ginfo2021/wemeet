package com.wemeet.dating.model.enums;

public enum FileType {
    PROFILE_IMAGE("PROFILE_IMAGE"),
    ADDITIONAL_IMAGE("ADDITIONAL_IMAGE"),
    MESSAGE_ATTACHMENT("MESSAGE_ATTACHMENT"),
    MUSIC("MUSIC");


    private final String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
