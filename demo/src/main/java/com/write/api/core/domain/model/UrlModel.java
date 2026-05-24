package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlModel {
    Long id;
    Long version;
    Long userId;
    String shortCode;
    String description;
    String faviconUrl;
    String originalUrl;
    String title;
    String domain;
    UrlStatusEnum status;
    UrlAccessTypeEnum accessType;
    String passwordHash;
    boolean customAlias;
    LocalDateTime deletedAt;
    LocalDateTime expiresAt;
    LocalDateTime lastAccessAt;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}