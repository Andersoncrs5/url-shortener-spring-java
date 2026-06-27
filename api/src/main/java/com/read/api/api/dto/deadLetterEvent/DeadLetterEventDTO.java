package com.read.api.api.dto.deadLetterEvent;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.DeadLetterStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeadLetterEventDTO extends BaseDTO {
    Long eventId;

    String sourceTopic;

    String targetDlqTopic;

    String eventType;

    String errorMessage;

    String stackTrace;

    Integer retryCount;

    String payload;

    Integer maxRetries;

    DeadLetterStatus status;

    LocalDateTime lastRetryAt;

    LocalDateTime resolvedAt;

    LocalDateTime nextRetryAt;

}
