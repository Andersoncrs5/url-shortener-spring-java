package com.write.api.application.dto.user;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        Long version,
        LocalDateTime createdAt,
        Set<String> roles
) {
}
