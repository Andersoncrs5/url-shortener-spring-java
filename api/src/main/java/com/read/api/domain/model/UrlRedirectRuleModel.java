package com.read.api.domain.model;

import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.MatchTypeEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlRedirectRuleModel extends BaseModel {
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
