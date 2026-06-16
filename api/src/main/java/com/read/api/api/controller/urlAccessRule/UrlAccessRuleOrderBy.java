package com.read.api.api.controller.urlAccessRule;

public enum UrlAccessRuleOrderBy {
    ID("id"),
    URL_ID("urlId"),
    TYPE("type"),
    RULE_VALUE("ruleValue"),
    ACTIVE("active"),
    ASSIFNED_BY_USER_ID("assignedByUserId"),
    EXPIRES_AT("expiresAt"),
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