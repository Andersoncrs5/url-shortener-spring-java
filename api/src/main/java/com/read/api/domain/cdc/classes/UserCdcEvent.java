package com.read.api.domain.cdc.classes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.infrastructure.config.jackson.Boolean01Deserializer;
import java.time.LocalDateTime;

public record UserCdcEvent(
        Long id,
        Long version,
        String name,
        String email,
        String refreshToken,
        String passwordHash,
        String roles,
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean active,
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean emailVerified,
        Integer attemptsLoginFailed,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime blockedAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastLoginAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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