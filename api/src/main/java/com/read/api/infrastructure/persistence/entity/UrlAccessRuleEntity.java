package com.read.api.infrastructure.persistence.entity;

import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "urlAccessRules")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlAccessRuleEntity extends BaseEntity {
    Long urlId;

    UrlAccessRuleTypeEnum type;

    String ruleValue;

    boolean active;

    Long assignedByUserId;

    LocalDateTime expiresAt;
}
