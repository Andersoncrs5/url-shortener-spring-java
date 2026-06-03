package com.write.api.application.dto.messaging;

public record OutboxEventMessage<T>(
        String eventId,
        String aggregateType,
        Long aggregateId,
        String eventType,
        String topic,
        T payload,
        long version
) {
}
