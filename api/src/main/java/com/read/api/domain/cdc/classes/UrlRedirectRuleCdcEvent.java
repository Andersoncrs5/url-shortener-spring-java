package com.read.api.domain.cdc.classes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.MatchTypeEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import com.read.api.infrastructure.config.jackson.Boolean01Deserializer;
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
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean active,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) implements BaseCdcEvent {
        public UrlRedirectRuleCdcEvent {
                if (countryCode == null) countryCode = "";
                if (region == null) region = "";
                if (redirectUrl == null) redirectUrl = "";
                if (ruleHash == null) ruleHash = "";
                if (priority == null) priority = 0;
                if (active == null) active = false;
        }
}