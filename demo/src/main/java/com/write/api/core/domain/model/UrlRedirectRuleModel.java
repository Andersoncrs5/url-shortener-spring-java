package com.write.api.core.domain.model;

import java.time.LocalDateTime;

public class UrlRedirectRuleModel {

    private Long id;
    private Long urlId;
    private String countryCode;
    private String deviceType;
    private String redirectUrl;
    private Integer priority;
    private boolean active;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
