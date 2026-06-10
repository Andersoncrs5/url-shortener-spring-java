package com.read.api.domain.model;

import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlModel extends BaseModel {

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

}
