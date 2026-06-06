package com.write.api.core.domain.enums;

public enum TopicEnum {

    NOTIFY_ADMINS("notify.admins"),

    URL_ACCESS_RULE_CREATED("url.access.rule.created"),

    URL_CREATED("url.created"),
    URL_DELETED("url.deleted"),
    URL_UPDATED("url.updated"),

    USER_CREATED("user.created"),
    USER_DELETED("user.deleted"),
    USER_BLOCKED("user.blocked"),
    USER_LOGIN_SUCCESS("user.login_success"),
    USER_LOGIN_FAILED("user.login_failed");

    private final String value;

    TopicEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String dlq() {
        return value + ".dlq";
    }
}

