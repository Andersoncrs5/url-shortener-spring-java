package com.write.api.core.domain.enums;

public enum TopicEnum {

    URL_CREATED("url.created"),
    URL_DELETED("url.deleted"),

    USER_CREATED("user.created"),
    USER_BLOCKED("user.blocked"),
    USER_LOGIN_SUCCESS("user.login_success"),
    USER_LOGIN_FAILED("user.login_failed"),
    USER_DELETED("user.deleted");

    private final String value;

    TopicEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}