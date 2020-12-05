package com.wemeet.dating.model.enums;

public enum SwipeType {
    LIKE("LIKE"),
    UNLIKE("UNLIKE");

    private final String name;

    SwipeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
