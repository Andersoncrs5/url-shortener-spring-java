package com.write.api.core.domain.model;

import java.time.Instant;

public class UrlModel {

    private Long id;
    private Long version;
    private Long userId;
    private String shortCode;
    private String originalUrl;
    private String title;
    private boolean active;
    private boolean customAlias;
    private boolean publicUrl;
    private Instant expiresAt;
    private Instant lastAccessAt;
    private Instant createdAt;
    private Instant updatedAt;

}