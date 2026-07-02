package com.read.api.api.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "BaseDTO", description = "Core foundational structural metadata properties inherited by all system response payload DTOs")
public class BaseDTO {

    @Schema(description = "The database primary tracking identifier assigned to the entity instance", example = "1024")
    Long id;

    @Schema(description = "The optimistic locking control version sequence counter used to mitigate transactional state conflicts", example = "1")
    Long version;

    @Schema(description = "Server clock timestamp tracking exactly when this entity was persisted into the storage layer", example = "2026-07-02T15:00:00")
    LocalDateTime createdAt;

    @Schema(description = "Server clock timestamp log marking the last chronological execution edit performed on this record data structure", example = "2026-07-02T15:45:10")
    LocalDateTime updatedAt;
}