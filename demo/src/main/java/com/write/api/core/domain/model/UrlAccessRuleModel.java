package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.model.shared.BaseModel;
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