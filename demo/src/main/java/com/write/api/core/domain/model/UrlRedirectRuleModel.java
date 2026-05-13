package com.write.api.core.domain.model;

import java.time.Instant;

public class UrlRedirectRuleModel {

    private Long id;
    private Long urlId;
    private String countryCode;
    private String deviceType;
    private String redirectUrl;
    private Integer priority;
    private boolean active;
    private Instant startAt;
    private Instant endAt;
    private Instant createdAt;
    private Instant updatedAt;
}
