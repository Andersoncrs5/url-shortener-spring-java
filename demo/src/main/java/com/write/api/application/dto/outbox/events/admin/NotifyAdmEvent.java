package com.write.api.application.dto.outbox.events.admin;

import java.time.LocalDateTime;

public record NotifyAdmEvent(
        Long outboxId,
        String aggregateType,
        String eventType,
        String topic,
        Integer retryCount,
        String errorMessage,
        LocalDateTime failedAt
) {

    public static NotifyAdmEvent create(
            Long outboxId,
            String aggregateType,
            String eventType,
            String topic,
            Integer retryCount,
            String errorMessage,
            LocalDateTime failedAt
    ) {
        return new NotifyAdmEvent(
                outboxId,
                aggregateType,
                eventType,
                topic,
                retryCount,
                errorMessage,
                failedAt
        );
    }
}