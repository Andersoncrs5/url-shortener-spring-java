package com.read.api.api.dto.url;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlDTO extends BaseDTO {

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
    UrlMetricDTO metric;
    LocalDateTime deletedAt;
    LocalDateTime expiresAt;
    LocalDateTime lastAccessAt;

}
