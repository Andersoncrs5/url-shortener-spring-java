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
@Schema(name = "BaseFilter", description = "Foundational dynamic pagination query matrix properties inherited by all target resource filter types")
public class BaseFilter {

    @Schema(description = "Filter results specifically matching an exact primary database unique identifier target", example = "1024")
    Long id;

    @Schema(description = "Starting date time range threshold boundary limit filtering structural record creation timestamps", example = "2026-07-01T00:00:00")
    LocalDateTime createdAtAfter;

    @Schema(description = "Ending date time range threshold boundary limit filtering structural record creation timestamps", example = "2026-07-02T23:59:59")
    LocalDateTime createdAtBefore;

    @Schema(description = "Starting date time range threshold boundary limit filtering structural record update modifications logs", example = "2026-07-01T00:00:00")
    LocalDateTime updatedAtAfter;

    @Schema(description = "Ending date time range threshold boundary limit filtering structural record update modifications logs", example = "2026-07-02T23:59:59")
    LocalDateTime updatedAtBefore;
}