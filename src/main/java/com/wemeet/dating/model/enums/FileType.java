package com.wemeet.dating.model.enums;

public enum FileType {
    PROFILE_IMAGE("PROFILE_IMAGE"),
    ADDITIONAL_IMAGE("ADDITIONAL_IMAGE"),
    PLAYLIST("PLAYLIST"),
    MUSIC("MUSIC"),
    ARTWORK("ARTWORK"),
    MESSAGE_ATTACHMENT("MESSAGE_ATTACHMENT");

    private final String name;

    FileType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
