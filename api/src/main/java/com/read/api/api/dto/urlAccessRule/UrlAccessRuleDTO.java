package com.read.api.api.dto.urlAccessRule;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlAccessRuleDTO extends BaseDTO {
    Long urlId;

    UrlAccessRuleTypeEnum type;

    String ruleValue;

    boolean active;

    Long assignedByUserId;

    LocalDateTime expiresAt;
}
