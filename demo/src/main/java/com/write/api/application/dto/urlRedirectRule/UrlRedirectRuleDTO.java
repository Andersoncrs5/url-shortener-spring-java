package com.write.api.application.dto.urlRedirectRule;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlRedirectRuleDTO(

        @IsId
        Long id,

        @IsId
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

) {
}