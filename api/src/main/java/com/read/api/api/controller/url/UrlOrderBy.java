package com.read.api.api.controller.url;

import lombok.Getter;

@Getter
public enum UrlOrderBy {
    ID("id"),
    USER_ID("userId"),
    SHORT_CODE("shortCode"),
    DESCRIPTION("description"),
    FAVICON_URL("faviconUrl"),
    ORIGINAL_URL("originalUrl"),
    TITLE("title"),
    DOMAIN("domain"),
    STATUS("status"),
    ACCESS_TYPE("accessType"),
    PASSWORD_HASH("passwordHash"),
    CUSTOM_ALIAS("customAlias"),
    DELETED_AT("deletedAt"),
    EXPIRES_AT("expiresAt"),
    LAST_ACCESS_AT("lastAccessAt"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String field;

    UrlOrderBy(String field) {
        this.field = field;
    }

}
