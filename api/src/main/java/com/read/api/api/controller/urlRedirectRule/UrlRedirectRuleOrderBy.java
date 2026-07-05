package com.read.api.api.controller.urlRedirectRule;

import com.read.api.api.controller.base.ConvertibleEnum;
import lombok.Getter;

@Getter
public enum UrlRedirectRuleOrderBy implements ConvertibleEnum {
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