package com.notify.notify.configs.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        @NotBlank String name,
        @NotBlank String nameShort,
        @NotBlank String description,
        @NotBlank String version,
        @NotBlank String environment
) {
}
