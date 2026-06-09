package com.read.api.domain.model;

import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiKeyModel extends BaseModel {

    Long userId;
    Long ownerUserId;
    String keyHash;
    String name;
    boolean active;
    LocalDateTime lastUsedAt;
    LocalDateTime expiresAt;

}
