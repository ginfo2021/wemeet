package com.wemeet.dating.model.enums;

public enum AccountType {

    FREE("FREE"),
    PREMIUM("PREMIUM");

    private final String name;

    AccountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
