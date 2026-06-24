package com.read.api.domain.cdc.classes;

import java.time.LocalDateTime;

public record UserCdcEvent(
        Long id,
        Long version,
        String name,
        String email,
        String refreshToken,
        String passwordHash,
        String roles,
        Boolean active,
        Boolean emailVerified,
        Integer attemptsLoginFailed,
        LocalDateTime blockedAt,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}