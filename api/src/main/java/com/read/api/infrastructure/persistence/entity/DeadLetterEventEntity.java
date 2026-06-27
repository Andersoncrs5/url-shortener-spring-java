package com.read.api.infrastructure.persistence.entity;

import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "dead_letter_events")
public class DeadLetterEventEntity extends BaseEntity {

    Long eventId;

    String sourceTopic;

    String targetDlqTopic;

    String eventType;

    String errorMessage;

    String stackTrace;

    String payload;

    Integer retryCount;

    Integer maxRetries;

    DeadLetterStatus status;

    LocalDateTime lastRetryAt;

    LocalDateTime nextRetryAt;

    LocalDateTime resolvedAt;
}