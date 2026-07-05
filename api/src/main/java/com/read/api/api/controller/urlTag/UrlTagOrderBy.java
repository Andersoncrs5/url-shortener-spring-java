package com.read.api.api.controller.urlTag;

import com.read.api.api.controller.base.ConvertibleEnum;
import lombok.Getter;

@Getter
public enum UrlTagOrderBy implements ConvertibleEnum {

    ID("id"),
    NAME("name"),
    SLUG("slug"),
    COLOR("color"),
    USER_ID("userId"),
    PARENT_ID("parentId"),
    ACTIVE("active"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UrlTagOrderBy(String field) {
        this.field = field;
    }

}