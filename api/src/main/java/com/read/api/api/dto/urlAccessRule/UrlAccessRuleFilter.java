package com.read.api.api.dto.urlAccessRule;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlAccessRuleFilter extends BaseFilter {
    Long urlId;

    UrlAccessRuleTypeEnum type;

    String ruleValue;

    Boolean active;

    Long assignedByUserId;

    LocalDateTime expiresAtAfter;
    LocalDateTime expiresAtBefore;
}
