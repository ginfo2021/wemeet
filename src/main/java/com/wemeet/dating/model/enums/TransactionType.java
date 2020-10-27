package com.wemeet.dating.model.enums;

public enum TransactionType {
    UPGRADE("UPGRADE");

    private final String name;

    TransactionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
