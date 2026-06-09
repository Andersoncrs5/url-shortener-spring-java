package com.read.api.api.dto.outbox;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.AggregateTypeEnum;
import com.read.api.domain.enums.EventTypeEnum;
import com.read.api.domain.enums.OutboxStatusEnum;
import com.read.api.domain.enums.TopicEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEventFilter extends BaseFilter {

    AggregateTypeEnum aggregateType;

    Long aggregateId;

    EventTypeEnum eventType;

    String payload;

    TopicEnum topic;

    OutboxStatusEnum status;

    Integer retryCountMin;

    Integer retryCountMax;

    String errorMessage;

    LocalDateTime nextRetryAtAfter;
    LocalDateTime nextRetryAtBefore;

    LocalDateTime processedAtAfter;
    LocalDateTime processedAtBefore;
}
