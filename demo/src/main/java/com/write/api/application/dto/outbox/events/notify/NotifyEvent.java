package com.write.api.application.dto.outbox.events.notify;

import com.write.api.core.domain.enums.NotificationChannel;
import com.write.api.core.domain.enums.NotificationPriority;

import java.time.LocalDateTime;
import java.util.List;

public record NotifyEvent(
        Long outboxId,
        String aggregateType,
        String eventType,
        String topic,
        String message,
        List<String> recipients,
        NotificationChannel channel,
        NotificationPriority priority,
        String correlationId,
        LocalDateTime failedAt
) {

    public static NotifyEvent create(
            Long outboxId,
            String aggregateType,
            String eventType,
            String topic,
            String message,
            List<String> recipient,
            NotificationChannel channel,
            NotificationPriority priority,
            String correlationId,
            LocalDateTime failedAt
    ) {
        return new NotifyEvent(
                outboxId,
                aggregateType,
                eventType,
                topic,
                message,
                recipient,
                channel,
                priority,
                correlationId,
                failedAt
        );
    }
}