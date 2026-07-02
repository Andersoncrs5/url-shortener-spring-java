package com.read.api.api.dto.urlAccessRule;

import com.read.api.api.dto.base.BaseFilter;
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
@Schema(name = "UrlAccessRuleFilter", description = "Dynamic criteria parameter matrix framework used to query and audit active link security access barriers")
public class UrlAccessRuleFilter extends BaseFilter {

    @Schema(description = "Isolate access rules mapped explicitly to a target specific shortened URL identifier", example = "1024")
    Long urlId;

    @Schema(description = "Filter rules sharing an identical operational restriction criteria behavior style", example = "GEOBLOCKING")
    UrlAccessRuleTypeEnum type;

    @Schema(description = "Pattern or exact match query targeting the payload comparison values context", example = "US")
    String ruleValue;

    @Schema(description = "Isolate restrictions based explicitly on current enforcement visibility states", example = "true")
    Boolean active;

    @Schema(description = "Find rule structures deployed by a specific individual profile identifier", example = "9921")
    Long assignedByUserId;

    @Schema(description = "Starting date time range boundary filtering rules set to terminate after a precise moment", example = "2026-07-01T00:00:00")
    LocalDateTime expiresAtAfter;

    @Schema(description = "Ending date time range boundary filtering rules set to terminate before a precise moment", example = "2026-07-02T23:59:59")
    LocalDateTime expiresAtBefore;
}