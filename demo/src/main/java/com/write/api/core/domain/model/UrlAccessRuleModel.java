package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;

import java.time.LocalDateTime;

public class UrlAccessRuleModel {

    private Long id;

    private Long urlId;

    private UrlAccessRuleTypeEnum type;

    private String value;

    private boolean active;

    private Long createdBy;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}