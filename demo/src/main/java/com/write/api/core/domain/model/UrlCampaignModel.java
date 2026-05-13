package com.write.api.core.domain.model;

import java.time.LocalDateTime;

public class UrlCampaignModel {

    private Long id;
    private Long userId;
    private String name;
    private String source;
    private String medium;
    private String campaign;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
