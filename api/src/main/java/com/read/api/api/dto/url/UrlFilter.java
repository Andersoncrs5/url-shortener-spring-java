package com.read.api.api.dto.url;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlFilter extends BaseFilter {
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
    Boolean customAlias;
    LocalDateTime deletedAtMin;
    LocalDateTime deletedAtMax;
    LocalDateTime expiresAtMin;
    LocalDateTime expiresAtMax;
    LocalDateTime lastAccessAtMin;
    LocalDateTime lastAccessAtMax;
}
