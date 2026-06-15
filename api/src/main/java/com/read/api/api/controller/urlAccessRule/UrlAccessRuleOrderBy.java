package com.read.api.api.controller.urlAccessRule;

public enum UrlAccessRuleOrderBy {
    ID("id"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UrlAccessRuleOrderBy(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}