package com.read.api.api.dto.deadLetterEvent;

import com.read.api.api.dto.base.BaseDTO;
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
@Schema(name = "DeadLetterEventDTO", description = "Detailed payload record of an asynchronous message processing failure routed to a Dead Letter Queue")
public class DeadLetterEventDTO extends BaseDTO {

    @Schema(description = "Unique functional event database identifier", example = "481023")
    Long eventId;

    @Schema(description = "The original broker topic origin name where the payload was initially consumed from", example = "v1.url.created")
    String sourceTopic;

    @Schema(description = "The dedicated recovery broker topic destination holding this corrupted state event record", example = "v1.url.created.dlq")
    String targetDlqTopic;

    @Schema(description = "The canonical internal system event mapping layout discriminator class name", example = "UrlCreatedEvent")
    String eventType;

    @Schema(description = "The concise summary extracted from the runtime caught Exception signature", example = "NullPointerException: Target domain metadata structure is missing")
    String errorMessage;

    @Schema(description = "The serialized core stack trace footprint snapshot tracking the system failure segment", example = "com.read.api.application.usecase.UrlUseCase.execute(UrlUseCase.java:42)...")
    String stackTrace;

    @Schema(description = "Current running amount of operational redelivery retry cycles executed by the consumer", example = "3")
    Integer retryCount;

    @Schema(description = "The literal raw immutable message payload object block serialized as plain text/JSON string", example = "{\"urlId\":1024,\"shortCode\":\"b7xK9p\"}")
    String payload;

    @Schema(description = "The maximum upper ceiling retry loop barrier allowed before suspending execution", example = "5")
    Integer maxRetries;

    @Schema(description = "The execution stage status metadata value monitoring this dead letter event record context", example = "PENDING_RETRY")
    DeadLetterStatus status;

    @Schema(description = "Timestamp when the last processing retry pipeline loop was triggered", example = "2026-07-02T14:30:00")
    LocalDateTime lastRetryAt;

    @Schema(description = "Timestamp indicating exactly when this failure context was mitigated, fixed or manually resolved", example = "2026-07-02T15:12:45")
    LocalDateTime resolvedAt;

    @Schema(description = "Next scheduled timestamp calculation window when the broker retry cron scheduler will poll this record", example = "2026-07-02T16:00:00")
    LocalDateTime nextRetryAt;
}