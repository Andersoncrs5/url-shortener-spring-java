package com.read.api.api.dto.urlRedirectRule;

import com.read.api.api.dto.base.BaseDTO;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.MatchTypeEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlRedirectRuleDTO", description = "Detailed structural parameters of a conditional redirection rule applied to a shortened link")
public class UrlRedirectRuleDTO extends BaseDTO {

    @Schema(description = "The database identifier of the target shortened URL bound to this routing condition", example = "1024")
    Long urlId;

    @Schema(description = "The ISO 3166-1 alpha-2 geographical country code trigger constraint", example = "BR", nullable = true)
    String countryCode;

    @Schema(description = "The state, province or region text identifier criteria matching the client footprint", example = "SP", nullable = true)
    String region;

    @Schema(description = "The macro geographic continent enum classifier constraint mapping", example = "SOUTH_AMERICA", nullable = true)
    ContinentEnum continent;

    @Schema(description = "The targeted desktop or mobile client operating system specification criteria", example = "ANDROID", nullable = true)
    OperatingSystemEnum os;

    @Schema(description = "The target user agent web browser core engine specification context", example = "CHROME", nullable = true)
    BrowserEnum browser;

    @Schema(description = "The strategic matching rule logic condition applied to execute this conditional mapping evaluation", example = "ANY")
    MatchTypeEnum matchType;

    @Schema(description = "The specific target destination URL alternate location address where traffic is rerouted if the conditions evaluate to true", example = "https://br.example.com/promo")
    String redirectUrl;

    @Schema(description = "A unique internal deterministic identity hash calculated over the rule payload to quickly check for duplicate conditions", example = "8aef31c4b90e")
    String ruleHash;

    @Schema(description = "The numerical sorting priority weight layer used to sort execution order when multiple rules match the request context (lower numbers indicate higher priority execution)", example = "10")
    Integer priority;

    @Schema(description = "The operational enforcement state visibility toggle tracking this conditional record redirection", example = "true")
    boolean active;

    @Schema(description = "The start timestamp window frame target after which this dynamic rule routing strategy becomes live and effective", example = "2026-07-01T00:00:00", nullable = true)
    LocalDateTime startAt;

    @Schema(description = "The absolute expiration timeline window indicating when this routing strategy must gracefully stop executing", example = "2026-12-31T23:59:59", nullable = true)
    LocalDateTime endAt;
}