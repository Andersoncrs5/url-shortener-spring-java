package com.notify.notify.configs.kafka.dlq;

import java.time.Instant;

public record DeadLetterEvent<T>(
        Long id,
        String topic,
        String error,
        Instant createdAt,
        T payload
) {
}
