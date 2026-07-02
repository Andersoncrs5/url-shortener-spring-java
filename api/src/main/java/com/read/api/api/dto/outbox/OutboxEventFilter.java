package com.read.api.api.dto.outbox;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.AggregateTypeEnum;
import com.read.api.domain.enums.EventTypeEnum;
import com.read.api.domain.enums.OutboxStatusEnum;
import com.read.api.domain.enums.TopicEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "OutboxEventFilter", description = "Dynamic criteria query matrix used to audit, filter, and track transactional outbox events state records")
public class OutboxEventFilter extends BaseFilter {

    @Schema(description = "Filter by the specific structural Domain Aggregate root entity classification", example = "URL")
    AggregateTypeEnum aggregateType;

    @Schema(description = "The database primary identifier instance value of the related business aggregate entity", example = "881920")
    Long aggregateId;

    @Schema(description = "Isolate records by the literal lifecycle operational event transaction behavior type", example = "URL_SHORTENED")
    EventTypeEnum eventType;

    @Schema(description = "Substring lookup parameter to locate metadata properties inside the stringified event JSON data block")
    String payload;

    @Schema(description = "The target destination routing channel or broker topic enum where this event is designed to be emitted", example = "V1_URL_TOPIC")
    TopicEnum topic;

    @Schema(description = "The operational delivery processing state monitoring this transactional record", example = "PENDING")
    OutboxStatusEnum status;

    @Schema(description = "Minimum inclusive processing retry count boundary selector for failing events", example = "0")
    Integer retryCountMin;

    @Schema(description = "Maximum inclusive processing retry count boundary selector for failing events", example = "3")
    Integer retryCountMax;

    @Schema(description = "Partial textual lookup pattern string to scan caught exception summaries within failed dispatch events", example = "Connection timeout")
    String errorMessage;

    @Schema(description = "Starting range threshold limit filtering scheduled automated engine re-delivery tasks", example = "2026-07-02T12:00:00")
    LocalDateTime nextRetryAtAfter;

    @Schema(description = "Ending range threshold limit filtering scheduled automated engine re-delivery tasks", example = "2026-07-02T18:00:00")
    LocalDateTime nextRetryAtBefore;

    @Schema(description = "Starting chronological timestamp boundary limit filtering successfully dispatched events", example = "2026-07-01T00:00:00")
    LocalDateTime processedAtAfter;

    @Schema(description = "Ending chronological timestamp boundary limit filtering successfully dispatched events", example = "2026-07-02T23:59:59")
    LocalDateTime processedAtBefore;
}