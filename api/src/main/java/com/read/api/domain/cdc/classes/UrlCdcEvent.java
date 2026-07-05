package com.read.api.domain.cdc.classes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.infrastructure.config.jackson.Boolean01Deserializer;

import java.time.LocalDateTime;

public record UrlCdcEvent(
        Long id,

        Long userId,
        String shortCode,
        String description,
        String faviconUrl,
        String originalUrl,
        String title,
        String domain,
        UrlStatusEnum status,
        UrlAccessTypeEnum accessType,
        String passwordHash,
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean customAlias,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime deletedAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expiresAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastAccessAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) implements BaseCdcEvent {
        public UrlCdcEvent {
                if (shortCode == null) shortCode = "";
                if (description == null) description = "";
                if (faviconUrl == null) faviconUrl = "";
                if (originalUrl == null) originalUrl = "";
                if (title == null) title = "";
                if (domain == null) domain = "";
                if (passwordHash == null) passwordHash = "";
                if (customAlias == null) customAlias = false;
                if (status == null) status = UrlStatusEnum.ACTIVE;
                if (accessType == null) accessType = UrlAccessTypeEnum.PUBLIC;
                if (createdAt == null) createdAt = LocalDateTime.now();
                if (updatedAt == null) updatedAt = LocalDateTime.now();
                if (id == null) {
                        throw new IllegalArgumentException("O campo ID não pode ser nulo no evento CDC.");
                }
        }
}
