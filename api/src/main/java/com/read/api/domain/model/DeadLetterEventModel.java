package com.read.api.domain.model;

import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeadLetterEventModel extends BaseModel {

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

    public static DeadLetterEventModel create(
            Long eventId,
            String sourceTopic,
            String targetDlqTopic,
            String eventType,
            String payload,
            Integer maxRetries
    ) {

        DeadLetterEventModel model = new DeadLetterEventModel();

        model.eventId = eventId;
        model.sourceTopic = sourceTopic;
        model.targetDlqTopic = targetDlqTopic;
        model.eventType = eventType;
        model.payload = payload;

        model.retryCount = 0;
        model.maxRetries = maxRetries;
        model.status = DeadLetterStatus.PENDING;

        return model;
    }

    public boolean canRetry() {
        return retryCount < maxRetries;
    }

    public void markAsProcessing() {

        status = DeadLetterStatus.PROCESSING;

        lastRetryAt =
                LocalDateTime.now();
    }

    public void markAsResolved() {

        status = DeadLetterStatus.PROCESSED;

        resolvedAt =
                LocalDateTime.now();
    }

    public void markAsFailed() {

        status = DeadLetterStatus.FAILED;

        lastRetryAt =
                LocalDateTime.now();
    }

    public void incrementRetry() {

        retryCount++;

        lastRetryAt =
                LocalDateTime.now();

        nextRetryAt =
                LocalDateTime.now()
                        .plusMinutes(
                                Math.min(
                                        retryCount * 5,
                                        60
                                )
                        );

        if (retryCount >= maxRetries) {

            status =
                    DeadLetterStatus.FAILED;
        }
    }
}