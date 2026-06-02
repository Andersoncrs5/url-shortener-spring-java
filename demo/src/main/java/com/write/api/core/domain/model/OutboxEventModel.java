package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OutboxEventModel extends BaseModel {

    private AggregateTypeEnum aggregateType;

    private Long aggregateId;

    private EventTypeEnum eventType;

    private String payload;

    private TopicEnum topic;

    private OutboxStatusEnum status;

    private Integer retryCount;

    private String errorMessage;

    private LocalDateTime nextRetryAt;

    private LocalDateTime processedAt;
}