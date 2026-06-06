package com.write.api.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxStatusEnum {

    PENDING("Pending"),
    PROCESSING("Processing"),
    PROCESSED("Processed"),
    RETRYING("Retrying"),
    FAILED("Failed"),
    FAILED_NOTIFICATION_SENT("Failed - Notification Sent");

    private final String description;
}