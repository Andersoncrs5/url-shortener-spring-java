package com.read.api.api.controller.urlAccessRule;

import com.read.api.api.controller.base.ConvertibleEnum;
import lombok.Getter;

@Getter
public enum UrlAccessRuleOrderBy implements ConvertibleEnum {
    ID("id"),
    URL_ID("urlId"),
    TYPE("type"),
    RULE_VALUE("ruleValue"),
    ACTIVE("active"),
    ASSIGNED_BY_USER_ID("assignedByUserId"),
    EXPIRES_AT("expiresAt"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UrlAccessRuleOrderBy(String field) {
        this.field = field;
    }

}