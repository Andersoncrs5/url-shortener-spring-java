package com.read.api.domain.model;


import com.read.api.domain.enums.AggregateTypeEnum;
import com.read.api.domain.enums.EventTypeEnum;
import com.read.api.domain.enums.OutboxStatusEnum;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboxEventModel extends BaseModel {

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