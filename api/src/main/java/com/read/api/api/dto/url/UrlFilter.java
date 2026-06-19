package com.read.api.api.dto.url;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
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
public class UrlFilter extends BaseFilter {
    boolean matchAllTags = false;

    Long userId;
    String shortCode;
    String description;
    String faviconUrl;
    String originalUrl;
    String title;
    String domain;
    UrlStatusEnum status;
    UrlAccessTypeEnum accessType;
    Set<String> tags = new HashSet<>();
    String passwordHash;
    Boolean customAlias;
    LocalDateTime deletedAtMin;
    LocalDateTime deletedAtMax;
    LocalDateTime expiresAtMin;
    LocalDateTime expiresAtMax;
    LocalDateTime lastAccessAtMin;
    LocalDateTime lastAccessAtMax;
}
