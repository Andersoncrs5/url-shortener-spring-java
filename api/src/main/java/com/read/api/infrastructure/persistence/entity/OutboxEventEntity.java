package com.read.api.infrastructure.persistence.entity;

import com.read.api.domain.enums.AggregateTypeEnum;
import com.read.api.domain.enums.EventTypeEnum;
import com.read.api.domain.enums.OutboxStatusEnum;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "outbox")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEventEntity extends BaseEntity {
    AggregateTypeEnum aggregateType;

    Long aggregateId;

    EventTypeEnum eventType;

    String payload;

    TopicEnum topic;

    OutboxStatusEnum status;

    Integer retryCount;

    String errorMessage;

    LocalDateTime nextRetryAt;

    LocalDateTime processedAt;
}
