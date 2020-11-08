package com.wemeet.dating.model.enums;

public enum MusicType {
    PLAYLIST("PLAYLIST"),
    REGULAR("REGULAR");

    private final String name;

    MusicType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
