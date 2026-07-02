package com.read.api.api.controller.deadLetterEvent;

import lombok.Getter;

@Getter
public enum DeadLetterEventOrderBy {

    ID("id"),
    EVENT_ID("eventId"),

    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),

    SOURCE_TOPIC("sourceTopic"),
    TARGET_DLQ_TOPIC("targetDlqTopic"),

    EVENT_TYPE("eventType"),

    STATUS("status"),

    RETRY_COUNT("retryCount"),
    MAX_RETRIES("maxRetries"),

    LAST_RETRY_AT("lastRetryAt"),
    NEXT_RETRY_AT("nextRetryAt"),
    RESOLVED_AT("resolvedAt");

    private final String field;

    DeadLetterEventOrderBy(String field) {
        this.field = field;
    }
}