package com.read.api.api.dto.urlAccessRule;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlAccessRuleDTO", description = "Detailed parameter representation of a structural access restriction rule linked to a shortened URL")
public class UrlAccessRuleDTO extends BaseDTO {

    @Schema(description = "The target database link owner identifier that this gateway constraint applies to", example = "1024")
    Long urlId;

    @Schema(description = "The specific behavior constraint or validation group profile type", example = "GEOBLOCKING")
    UrlAccessRuleTypeEnum type;

    @Schema(description = "The exact raw comparison criteria argument matching payload required by the verification engine", example = "US")
    String ruleValue;

    @Schema(description = "The active operational enforcement state flag monitoring this restriction layer", example = "true")
    boolean active;

    @Schema(description = "The database primary identifier of the admin or user account who attached this constraint", example = "9921")
    Long assignedByUserId;

    @Schema(description = "The absolute validation timeline deadline after which this gatekeeping rule automatically expires", example = "2026-12-31T23:59:59", nullable = true)
    LocalDateTime expiresAt;
}