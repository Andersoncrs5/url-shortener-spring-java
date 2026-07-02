package com.read.api.api.dto.url;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlFilter", description = "Dynamic matrix filter parameter structure used to audit, search, and map system encurtador records")
public class UrlFilter extends BaseFilter {

    @Schema(description = "Logical matching toggle. If True, filters records that contain ALL requested tags. If False, checks for ANY match.", example = "false")
    boolean matchAllTags = false;

    @Schema(description = "Filter records owned specifically by an explicit unique user profile ID", example = "9921")
    Long userId;

    @Schema(description = "Look up a record matching an exact unique routing short code slug", example = "b7xK9p")
    String shortCode;

    @Schema(description = "Partial textual pattern lookup match for scanning records description parameters")
    String description;

    @Schema(description = "Exact image location match string tracking targeted link icon parameters")
    String faviconUrl;

    @Schema(description = "Partial or complete text search targeting target destination long deep URLs strings")
    String originalUrl;

    @Schema(description = "Substring lookup parameter string targeting structural scraped target metadata page titles")
    String title;

    @Schema(description = "Filter elements linked to a clean explicit target root domain server host name", example = "example.com")
    String domain;

    @Schema(description = "Isolate records bound to a specific runtime execution visibility status context", example = "ACTIVE")
    UrlStatusEnum status;

    @Schema(description = "Isolate records matching a specific privacy protection level constraint profile", example = "PUBLIC")
    UrlAccessTypeEnum accessType;

    @Schema(description = "Set matrix collection containing tag category string name tokens used for analytical lookups")
    Set<String> tags = new HashSet<>();

    @Schema(description = "Internal security credential match lookup search parameter string")
    String passwordHash;

    @Schema(description = "Isolate records by custom slug identifier configuration assignments", example = "true")
    Boolean customAlias;

    @Schema(description = "Starting soft deletion chronological execution frame limit boundary", example = "2026-07-01T00:00:00")
    LocalDateTime deletedAtMin;

    @Schema(description = "Ending soft deletion chronological execution frame limit boundary", example = "2026-07-02T23:59:59")
    LocalDateTime deletedAtMax;

    @Schema(description = "Starting range boundary checking link expiration timelines", example = "2026-07-01T00:00:00")
    LocalDateTime expiresAtMin;

    @Schema(description = "Ending range boundary checking link expiration timelines", example = "2026-07-02T23:59:59")
    LocalDateTime expiresAtMax;

    @Schema(description = "Starting timestamp threshold boundary monitoring link engagement lookups", example = "2026-07-01T00:00:00")
    LocalDateTime lastAccessAtMin;

    @Schema(description = "Ending timestamp threshold boundary monitoring link engagement lookups", example = "2026-07-02T23:59:59")
    LocalDateTime lastAccessAtMax;
}