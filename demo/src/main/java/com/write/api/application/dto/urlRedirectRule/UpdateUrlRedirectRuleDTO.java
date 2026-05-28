package com.write.api.application.dto.urlRedirectRule;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateUrlRedirectRuleDTO(

        @Size(max = 2, message = "countryCode must have at most 2 characters")
        String countryCode,

        @Size(max = 100, message = "region must have at most 100 characters")
        String region,

        ContinentEnum continent,

        OperatingSystemEnum os,

        BrowserEnum browser,

        MatchTypeEnum matchType,

        @Size(max = 2048, message = "redirectUrl exceeded 2048 characters")
        String redirectUrl,

        @Positive(message = "priority must be greater than 0")
        Integer priority,

        Boolean active,

        java.time.LocalDateTime startAt,

        java.time.LocalDateTime endAt

) {
}