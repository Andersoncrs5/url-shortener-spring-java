package com.write.api.core.domain.enums;

public enum NotificationPriority {
    LOW("low"),
    NORMAL("normal"),
    HIGH("high"),
    CRITICAL("critical");

    private final String value;

    NotificationPriority(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public String dlq() {
        return value + ".dlq";
    }
}
