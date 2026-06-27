package com.read.api.api.dto.deadLetterEvent;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.DeadLetterStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeadLetterEventFilter extends BaseFilter {
    Long eventId;

    String sourceTopic;

    String targetDlqTopic;

    String eventType;

    String errorMessage;

    String stackTrace;

    Integer retryCountMin;
    Integer retryCountMax;

    Integer maxRetriesMin;
    Integer maxRetriesMax;

    DeadLetterStatus status;

    LocalDateTime lastRetryAtMin;
    LocalDateTime lastRetryAtMax;

    LocalDateTime resolvedAtMin;
    LocalDateTime resolvedAtMax;

    LocalDateTime nextRetryAtMin;
    LocalDateTime nextRetryAtMax;
}
