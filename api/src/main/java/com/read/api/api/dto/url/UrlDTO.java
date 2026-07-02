package com.read.api.api.dto.url;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlDTO", description = "Comprehensive metadata core representation of a shortened link entry record")
public class UrlDTO extends BaseDTO {

    @Schema(description = "The database primary identifier of the user who owns this shortened link mapping", example = "9921")
    Long userId;

    @Schema(description = "The unique alphanumeric slug token representing the shortened routing path key", example = "b7xK9p")
    String shortCode;

    @Schema(description = "An internal descriptive annotation summary detailing the purpose of this link", example = "Q3 Product Launch Campaign Link")
    String description;

    @Schema(description = "The resolved direct image source URL pointing to the destination target web favicon asset", example = "https://example.com/favicon.ico")
    String faviconUrl;

    @Schema(description = "The original raw destination target deep link URL where traffic will be rerouted", example = "https://example.com/products/launch-2026?ref=api")
    String originalUrl;

    @Schema(description = "The extracted web document metadata header title scraped from the destination target site", example = "Launch Products | Example Group")
    String title;

    @Schema(description = "The clean root structural host domain string parsed from the original target link destination", example = "example.com")
    String domain;

    @Schema(description = "The active operational lifecycle visibility state monitoring this short link", example = "ACTIVE")
    UrlStatusEnum status;

    @Schema(description = "The specific security privacy lookup behavior configuration rule applied to this link", example = "PROTECTED")
    UrlAccessTypeEnum accessType;

    @Schema(description = "The secure algorithmic hash token verification payload string if access type requires passwords", example = "$2a$12$L7pY...", nullable = true)
    String passwordHash;

    @Schema(description = "A boolean flag indicating if the shortCode token string was generated as a custom client user alias override", example = "true")
    boolean customAlias;

    @Schema(description = "The embedded structured composite metrics matrix analytical dashboard payload layer")
    UrlMetricDTO metric;

    @Schema(description = "The soft deletion chronological server timestamp log marker", example = "2026-07-02T12:00:00", nullable = true)
    LocalDateTime deletedAt;

    @Schema(description = "The validation expiration deadline window constraint token marking link lifecycle death", example = "2026-12-31T23:59:59", nullable = true)
    LocalDateTime expiresAt;

    @Schema(description = "Timestamp tracking the exact historical moment the link gateway resolve path was successfully processed", example = "2026-07-02T15:45:10", nullable = true)
    LocalDateTime lastAccessAt;
}