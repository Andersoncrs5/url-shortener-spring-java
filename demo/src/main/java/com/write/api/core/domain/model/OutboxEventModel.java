package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.shared.BaseModel;
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