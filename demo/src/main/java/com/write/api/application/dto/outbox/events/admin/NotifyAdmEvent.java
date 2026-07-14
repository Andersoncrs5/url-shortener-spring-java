package com.write.api.application.dto.outbox.events.admin;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record NotifyAdmEvent(
        @IsId Long outboxId,
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