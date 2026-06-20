package com.read.api.domain.model.metrics;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.base.BaseMetric;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlMetricModel extends BaseMetric {

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

    public void incrementBlocked(
            UrlAccessRuleTypeEnum type
    ) {
        increment(blockedByRule, type);
    }

    public void incrementBrowser(
            BrowserEnum browser
    ) {
        increment(browsers, browser);
    }

    public void incrementOperatingSystem(
            OperatingSystemEnum os
    ) {
        increment(operatingSystems, os);
    }

    public void incrementContinent(
            ContinentEnum continent
    ) {
        increment(continents, continent);
    }

    public void incrementAccessRuleCount() {
        accessRuleCount++;
    }

    public void decrementAccessRuleCount() {
        if (accessRuleCount > 0) {
            accessRuleCount--;
        }
    }

    public void incrementRedirectRuleCount() {
        redirectRuleCount++;
    }

    public void decrementRedirectRuleCount() {
        if (redirectRuleCount > 0) {
            redirectRuleCount--;
        }
    }

    public void incrementTagCount() {
        tagCount++;
    }

    public void decrementTagCount() {
        if (tagCount > 0) {
            tagCount--;
        }
    }

    public void incrementRedirects() {
        redirects++;
    }

    public void decrementRedirects() {
        if (redirects > 0) {
            redirects--;
        }
    }

}
