package com.wemeet.dating.model.enums;

public enum WorkStatus {

    WORKING("Working"),
    SELF_EMPLOYED("Self-Employed"),
    STUDENT("Student"),
    UNEMPLOYED("Unemployed");

    private final String name;

    WorkStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
