package com.write.api.application.dto.outbox;

import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotNull;

public record CreateOutboxEventCommand(
        AggregateTypeEnum aggregateType,

        @IsId
        Long aggregateId,
        EventTypeEnum eventType,
        TopicEnum topic,

        @NotNull
        Object payload
) {
}