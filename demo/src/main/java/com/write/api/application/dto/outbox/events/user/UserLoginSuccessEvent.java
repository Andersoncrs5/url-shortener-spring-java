package com.write.api.application.dto.outbox.events.user;

import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotBlank;

public record UserLoginSuccessEvent(
        @IsId
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String email
) {
    public static UserLoginSuccessEvent create(Long id, String name, String email) {
        return new UserLoginSuccessEvent(id, name, email);
    }
}