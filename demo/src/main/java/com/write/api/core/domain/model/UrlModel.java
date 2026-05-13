package com.write.api.core.domain.model;

import java.time.LocalDateTime;

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
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}