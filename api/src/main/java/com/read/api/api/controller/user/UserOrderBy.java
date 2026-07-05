package com.read.api.api.controller.user;

import com.read.api.api.controller.base.ConvertibleEnum;
import lombok.Getter;

@Getter
public enum UserOrderBy implements ConvertibleEnum {
    ID("id"),
    NAME("name"),
    EMAIL("email"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UserOrderBy(String field) {
        this.field = field;
    }

}