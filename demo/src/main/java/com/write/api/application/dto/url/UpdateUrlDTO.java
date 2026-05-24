package com.write.api.application.dto.url;

import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateUrlDTO(

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

        UrlStatusEnum status,

        UrlAccessTypeEnum accessType,

        @Size(min = 4, max = 255)
        String password,

        LocalDateTime expiresAt

) {
}