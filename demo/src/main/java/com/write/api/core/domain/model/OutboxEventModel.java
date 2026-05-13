package com.write.api.core.domain.model;

import java.time.LocalDateTime;

public class OutboxEventModel {

    private Long id;
    private String aggregateType;
    private Long aggregateId;
    private String eventType;
    private String payload;
    private String status;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}