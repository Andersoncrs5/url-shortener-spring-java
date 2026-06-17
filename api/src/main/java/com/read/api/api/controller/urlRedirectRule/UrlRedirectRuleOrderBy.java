package com.read.api.api.controller.urlRedirectRule;

import lombok.Getter;

@Getter
public enum UrlRedirectRuleOrderBy {
    ID("id"),
    URL_ID("urlId"),
    PRIORITY("priority"),
    ACTIVE("active"),
    START_AT("startAt"),
    END_AT("endAt"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UrlRedirectRuleOrderBy(String field) {
        this.field = field;
    }

}