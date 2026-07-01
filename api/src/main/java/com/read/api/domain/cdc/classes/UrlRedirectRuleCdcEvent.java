package com.read.api.domain.cdc.classes;

import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.MatchTypeEnum;
import com.read.api.domain.enums.OperatingSystemEnum;

import java.time.LocalDateTime;

public record UrlRedirectRuleCdcEvent(
        Long id,
        Long urlId,
        String countryCode,
        String region,
        ContinentEnum continent,
        OperatingSystemEnum os,
        BrowserEnum browser,
        MatchTypeEnum matchType,
        String redirectUrl,
        String ruleHash,
        Integer priority,
        boolean active,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseCdcEvent {
}
