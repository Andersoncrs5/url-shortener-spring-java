package com.read.api.api.dto.deadLetterEvent;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.DeadLetterStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "DeadLetterEventFilter", description = "Dynamic matrix parameter structure used to query and filter archived consumer cluster failure items")
public class DeadLetterEventFilter extends BaseFilter {

    @Schema(description = "Filter specifically by a precise unique failure event ID location match", example = "481023")
    Long eventId;

    @Schema(description = "Substring match target for filtering originating producer broker channel paths", example = "url")
    String sourceTopic;

    @Schema(description = "Substring match target for tracking specific recovery DLQ destination queues", example = "dlq")
    String targetDlqTopic;

    @Schema(description = "Dynamic token matching parameter lookup for the message class event identifier type", example = "UrlCreatedEvent")
    String eventType;

    @Schema(description = "Partial textual pattern lookup match for locating matching exception messages inside logs", example = "NullPointerException")
    String errorMessage;

    @Schema(description = "Search criteria to query specific target keywords inside archived application crash stacktraces")
    String stackTrace;

    @Schema(description = "Minimum inclusive processing retry count boundary selector", example = "0")
    Integer retryCountMin;

    @Schema(description = "Maximum inclusive processing retry count boundary selector", example = "3")
    Integer retryCountMax;

    @Schema(description = "Minimum allowed max retries design constraint setup parameter match", example = "5")
    Integer maxRetriesMin;

    @Schema(description = "Maximum allowed max retries design constraint setup parameter match", example = "10")
    Integer maxRetriesMax;

    @Schema(description = "Isolate records bound to a specific runtime execution lifecycle state", example = "FAILED")
    DeadLetterStatus status;

    @Schema(description = "Starting date time threshold boundary for tracking historical retry attempts", example = "2026-07-01T00:00:00")
    LocalDateTime lastRetryAtMin;

    @Schema(description = "Ending date time threshold boundary for tracking historical retry attempts", example = "2026-07-02T23:59:59")
    LocalDateTime lastRetryAtMax;

    @Schema(description = "Starting date time window limit filtering resolved mitigation events", example = "2026-07-01T00:00:00")
    LocalDateTime resolvedAtMin;

    @Schema(description = "Ending date time window limit filtering resolved mitigation events", example = "2026-07-02T23:59:59")
    LocalDateTime resolvedAtMax;

    @Schema(description = "Starting range boundary marking expected automated engine recovery executions", example = "2026-07-02T12:00:00")
    LocalDateTime nextRetryAtMin;

    @Schema(description = "Ending range boundary marking expected automated engine recovery executions", example = "2026-07-02T18:00:00")
    LocalDateTime nextRetryAtMax;
}