package com.write.api.core.domain.model;

import java.time.Instant;

public class ApyKeyModel {
    private Long id;
    private Long version;
    private Long userId;
    private String keyHash;
    private String name;
    private boolean active;
    private Instant lastUsedAt;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
}
