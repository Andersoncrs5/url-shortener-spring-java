package com.write.api.core.domain.enums;

public enum TopicEnum {
    API_KEY_CREATED("api.key.created"),
    API_KEY_DELETED("api.key.deleted"),

    NOTIFY_ADMINS("notify.admins"),
    NOTIFY("notify"),

    URL_ACCESS_RULE_CREATED("url.access.rule.created"),
    URL_ACCESS_RULE_DELETED("url.access.rule.deleted"),

    URL_REDIRECT_RULE_CREATED("url.redirect.rule.created"),
    URL_REDIRECT_RULE_DELETED("url.redirect.rule.deleted"),

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

