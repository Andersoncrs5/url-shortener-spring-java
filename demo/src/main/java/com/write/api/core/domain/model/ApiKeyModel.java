package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiKeyModel extends BaseModel {
    private Long userId;
    private Long ownerUserId;
    private String keyHash;
    private String name;
    private boolean active;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;
}
