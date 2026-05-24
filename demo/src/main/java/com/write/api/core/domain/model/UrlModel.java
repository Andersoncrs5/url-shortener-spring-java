package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;

import java.time.LocalDateTime;

public class UrlModel {

    private Long id;
    private Long version;
    private Long userId;
    private String shortCode;
    private String description;
    private String faviconUrl;
    private String originalUrl;
    private String title;
    private String domain;
    private UrlStatusEnum status;
    private UrlAccessTypeEnum accessType;
    private String passwordHash;
    private boolean customAlias;
    private LocalDateTime deletedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}