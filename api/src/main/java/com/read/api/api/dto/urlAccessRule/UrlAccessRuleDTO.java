package com.read.api.api.dto.urlAccessRule;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlAccessRuleDTO extends BaseDTO {
    Long urlId;

    UrlAccessRuleTypeEnum type;

    String ruleValue;

    boolean active;

    Long assignedByUserId;

    LocalDateTime expiresAt;
}
