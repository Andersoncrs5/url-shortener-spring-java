package com.write.api.core.domain.model;

import java.time.Instant;

public class OutboxEventModel {

    private Long id;
    private String aggregateType;
    private Long aggregateId;
    private String eventType;
    private String payload;
    private String status;
    private Instant processedAt;
    private Instant createdAt;
}