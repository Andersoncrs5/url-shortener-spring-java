package com.write.api.core.domain.model;

import java.time.LocalDateTime;

public class ApyKeyModel {
    private Long id;
    private Long version;
    private Long userId;
    private String keyHash;
    private String name;
    private boolean active;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
