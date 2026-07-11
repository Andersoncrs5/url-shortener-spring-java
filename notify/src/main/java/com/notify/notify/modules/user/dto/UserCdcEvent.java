package com.notify.notify.modules.user.dto;

import com.notify.notify.utils.base.cdc.BaseCdcEvent;

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
) implements BaseCdcEvent {
    public UserCdcEvent {
        if (name == null) name = "";
        if (email == null) email = "";
        if (refreshToken == null) refreshToken = "";
        if (passwordHash == null) passwordHash = "";
        if (roles == null) roles = "";
        if (active == null) active = false;
        if (emailVerified == null) emailVerified = false;
        if (attemptsLoginFailed == null) attemptsLoginFailed = 0;
        if (version == null) version = 0L;
    }
}
