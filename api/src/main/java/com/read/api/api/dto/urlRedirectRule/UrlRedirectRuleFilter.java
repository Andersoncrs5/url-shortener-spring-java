package com.read.api.api.dto.urlRedirectRule;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.MatchTypeEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlRedirectRuleFilter", description = "Dynamic matrix filter parameter mapping structure used to audit, track, and scan conditional link routing behaviors")
public class UrlRedirectRuleFilter extends BaseFilter {

    @Schema(description = "Isolate redirect rules assigned to an explicit core unique shortened URL ID location", example = "1024")
    Long urlId;

    @Schema(description = "Filter rules configured specifically to match a precise country criteria parameter code", example = "BR")
    String countryCode;

    @Schema(description = "Filter rules configured specifically to target a clean region text identifier string", example = "SP")
    String region;

    @Schema(description = "Isolate routing rules mapped onto a specific continent demographic environment", example = "SOUTH_AMERICA")
    ContinentEnum continent;

    @Schema(description = "Isolate rules targeting specific client runtime ecosystem platforms", example = "ANDROID")
    OperatingSystemEnum os;

    @Schema(description = "Isolate rules targeting specific engine user agent platforms", example = "CHROME")
    BrowserEnum browser;

    @Schema(description = "Query rules using an explicit rule match logic behavior profile alignment", example = "ANY")
    MatchTypeEnum matchType;

    @Schema(description = "Partial or exact match lookup string targeting alternative redirect destinations tracking targets")
    String redirectUrl;

    @Schema(description = "Exact match lookup query parameter checking precise constraint state hashes", example = "8aef31c4b90e")
    String ruleHash;

    @Schema(description = "Isolate rules assigned to a precise prioritization engine slot position ranking weight", example = "10")
    Integer priority;

    @Schema(description = "Isolate configurations based on current deployment operational execution lifecycle states", example = "true")
    Boolean active;

    @Schema(description = "Starting date range filter threshold capturing rules activated after a specific timestamp window", example = "2026-07-01T00:00:00")
    LocalDateTime startAtAfter;

    @Schema(description = "Ending date range filter threshold capturing rules activated before a specific timestamp window", example = "2026-07-02T23:59:59")
    LocalDateTime startAtBefore;

    @Schema(description = "Starting date range filter threshold capturing rules scheduled to expire after a specific timestamp window", example = "2026-07-01T00:00:00")
    LocalDateTime endAtAfter;

    @Schema(description = "Ending date range filter threshold capturing rules scheduled to expire before a specific timestamp window", example = "2026-07-02T23:59:59")
    LocalDateTime endAtBefore;
}