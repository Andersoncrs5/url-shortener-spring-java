package com.read.api.infrastructure.persistence.entity;

import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlEntity extends BaseEntity {
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
    Set<String> tags = new HashSet<>();
    boolean customAlias;
    LocalDateTime deletedAt;
    LocalDateTime expiresAt;
    LocalDateTime lastAccessAt;
}
