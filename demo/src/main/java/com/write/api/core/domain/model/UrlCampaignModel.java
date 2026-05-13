package com.write.api.core.domain.model;

import java.time.Instant;

public class UrlCampaignModel {

    private Long id;
    private Long userId;
    private String name;
    private String source;
    private String medium;
    private String campaign;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
