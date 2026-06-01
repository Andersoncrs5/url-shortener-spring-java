package com.write.api.application.dto.apiKey;

import java.time.LocalDateTime;

public record UpdateApiKeyDTO(

        String name,

        Boolean active,

        LocalDateTime expiresAt

) {
}