package com.notify.notify.globals.classes.outbox;

public record OutboxCdcEvent(
        String eventId,
        String aggregateType,
        Long aggregateId,
        String eventType,
        String topic,
        String payload,
        Integer version
) {}