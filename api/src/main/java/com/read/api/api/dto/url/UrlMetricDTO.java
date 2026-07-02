package com.read.api.api.dto.url;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.read.api.api.dto.metric.BaseMetricDTO;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlMetricDTO", description = "Aggregated analytical diagnostic counters tracking platform interactions, client runtimes, and location metadata")
public class UrlMetricDTO extends BaseMetricDTO {

    @Schema(description = "Total counter tracking all successful clicks and complete redirection operations", example = "154032")
    Long redirects = 0L;

    @Schema(description = "The total counter of active custom redirect rules configured for this context link", example = "2")
    Long redirectRuleCount = 0L;

    @Schema(description = "The total counter of active security or gatekeeping validation access rules assigned to this context link", example = "1")
    Long accessRuleCount = 0L;

    @Schema(description = "Total number of semantic tags classification labels assigned to this resource", example = "3")
    Long tagCount = 0L;

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    @Schema(description = "Map counter distributing access volume across different user client browser engines", example = "{\"CHROME\": 1200, \"FIREFOX\": 340}")
    Map<BrowserEnum, Long> browsers = new HashMap<>();

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    @Schema(description = "Map counter distributing interaction clicks logs across operating systems profiles", example = "{\"WINDOWS\": 900, \"MACOS\": 400, \"IOS\": 240}")
    Map<OperatingSystemEnum, Long> operatingSystems = new HashMap<>();

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    @Schema(description = "Geographic map metric structure aggregating audience volume metrics across continents context parameters", example = "{\"SOUTH_AMERICA\": 1500, \"EUROPE\": 43}")
    Map<ContinentEnum, Long> continents = new HashMap<>();

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    @Schema(description = "Audit counter mapping exactly how many incoming malicious or disallowed interactions were halted by specific access guard rules", example = "{\"GEOBLOCKING\": 12, \"PASSWORD_INVALID\": 3}")
    Map<UrlAccessRuleTypeEnum, Long> blockedByRule = new HashMap<>();
}