package com.wemeet.dating.model.enums;

public enum ReportType {
    ABUSIVE("ABUSIVE"),
    FAKE_PROFILE("FAKE_PROFILE"),
    HARASSMENT("HARASSMENT"),
    OTHERS("OTHERS");

    private final String name;

    ReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
