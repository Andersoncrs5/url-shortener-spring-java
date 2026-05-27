package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.core.domain.model.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlRedirectRuleModel extends BaseModel {

    private Long urlId;

    private String countryCode;
    private String region;
    private ContinentEnum continent;

    private OperatingSystemEnum os;
    private BrowserEnum browser;

    private MatchTypeEnum matchType;

    private String redirectUrl;
    private String ruleHash;
    private Integer priority;

    private boolean active;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
}