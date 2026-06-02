package com.write.api.application.dto.outbox.events.user;

import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotBlank;

public record UserLoginBlockedEvent(
        @IsId
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String email
) {
    public static UserLoginBlockedEvent create(Long id, String name, String email) {
        return new UserLoginBlockedEvent(id, name, email);
    }
}