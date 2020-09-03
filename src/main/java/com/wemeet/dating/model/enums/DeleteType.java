package com.wemeet.dating.model.enums;

public enum DeleteType {

    SELF("SELF"),
    ADMIN("ADMIN");

    private final String name;

    DeleteType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
