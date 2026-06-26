package com.read.api.domain.enums;

public enum TopicEnum {
    NOTIFY_EVENT_FAILED("notify.event.failed"),
    NOTIFY_EVENT_FAILED_DLQ("notify.event.failed.dlq"),

    USERS("users"),
    USERS_DLQ("users.dlq"),

    URL_TAG("url_tags"),
    URL_TAG_DLQ("url_tags.dlq"),

    URL_ACCESS_RULE("url_access_rule"),
    URL_ACCESS_RULE_DLQ("url_access_rule.dlq"),

    URL_REDIRECT_RULE("url_redirect_rules"),
    URL_REDIRECT_RULE_DLQ("url_redirect_rules.dlq"),

    URLS("urls"),
    URLS_DLQ("urls.dlq"),

    ROLES("roles"),
    ROLES_DLQ("roles.dlq");

    private final String value;

    TopicEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

