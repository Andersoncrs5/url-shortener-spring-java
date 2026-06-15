package com.read.api.api.controller.user;

public enum UserOrderBy {
    ID("id"),
    NAME("name"),
    EMAIL("email"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UserOrderBy(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}