package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.model.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlAccessRuleModel extends BaseModel {

    private Long urlId;

    private UrlAccessRuleTypeEnum type;

    private String ruleValue;

    private boolean active;

    private Long assignedByUserId;

    private LocalDateTime expiresAt;
}