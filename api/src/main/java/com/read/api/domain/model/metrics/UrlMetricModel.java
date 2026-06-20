package com.read.api.domain.model.metrics;

import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlMetricModel {

    Long urlId;

    long redirects;
    long uniqueVisitors;

    Map<BrowserEnum, Long> browsers = new EnumMap<>(BrowserEnum.class);
    Map<OperatingSystemEnum, Long> operatingSystems = new EnumMap<>(OperatingSystemEnum.class);
    Map<String, Long> countries = new HashMap<>();

    public void incrementBrowser(
            BrowserEnum browser
    ) {
        browsers.merge(browser, 1L, Long::sum);
    }

    public void incrementOperatingSystem(
            OperatingSystemEnum os
    ) {
        operatingSystems.merge(os, 1L, Long::sum);
    }

    public void incrementCountry(
            String country
    ) {
        countries.merge(country, 1L, Long::sum);
    }
}
