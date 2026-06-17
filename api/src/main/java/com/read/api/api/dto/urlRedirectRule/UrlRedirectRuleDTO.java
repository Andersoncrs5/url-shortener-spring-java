package com.read.api.api.dto.urlRedirectRule;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.MatchTypeEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlRedirectRuleDTO extends BaseDTO {
    Long urlId;

    String countryCode;
    String region;
    ContinentEnum continent;

    OperatingSystemEnum os;
    BrowserEnum browser;

    MatchTypeEnum matchType;

    String redirectUrl;
    String ruleHash;
    Integer priority;

    boolean active;

    LocalDateTime startAt;
    LocalDateTime endAt;
}
