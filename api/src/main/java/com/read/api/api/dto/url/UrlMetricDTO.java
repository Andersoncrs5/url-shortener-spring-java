package com.read.api.api.dto.url;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.read.api.api.dto.metric.BaseMetricDTO;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlMetricDTO extends BaseMetricDTO {
    Long redirects = 0L;

    Long redirectRuleCount = 0L;
    Long accessRuleCount = 0L;
    Long tagCount = 0L;

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    Map<BrowserEnum, Long> browsers = new HashMap<>();

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    Map<OperatingSystemEnum, Long> operatingSystems = new HashMap<>();

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    Map<ContinentEnum, Long> continents = new HashMap<>();

    @JsonSerialize(keyUsing = JsonSerializer.None.class)
    Map<UrlAccessRuleTypeEnum, Long> blockedByRule = new HashMap<>();

}
