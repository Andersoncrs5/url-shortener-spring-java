package com.write.api.application.dto.outbox.events.user;

import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotBlank;

public record UserCreatedEvent(
        @IsId
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String email
) {
    public static UserCreatedEvent create(Long id, String name, String email) {
        return new UserCreatedEvent(id, name, email);
    }
}