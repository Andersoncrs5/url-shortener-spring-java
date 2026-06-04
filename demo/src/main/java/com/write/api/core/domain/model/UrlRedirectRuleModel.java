package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.core.domain.model.shared.BaseModel;
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