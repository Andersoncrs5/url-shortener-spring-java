package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
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
