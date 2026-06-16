package com.read.api.domain.model;

import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlAccessRuleModel extends BaseModel {
    Long urlId;

    UrlAccessRuleTypeEnum type;

    String ruleValue;

    boolean active;

    Long assignedByUserId;

    LocalDateTime expiresAt;
}
