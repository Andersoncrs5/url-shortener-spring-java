package com.write.api.application.dto.url;

import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateUrlDTO(

        @NotBlank
        @Size(max = 2048)
        String originalUrl,

        @Size(max = 255)
        String title,

        @Size(max = 500)
        String description,

        @Size(max = 500)
        String faviconUrl,

        @Size(max = 255)
        String domain,

        @Size(min = 3, max = 120)
        String shortCode,

        @NotNull
        UrlAccessTypeEnum accessType,

        @Size(min = 4, max = 255)
        String password,

        LocalDateTime expiresAt

) {
}
