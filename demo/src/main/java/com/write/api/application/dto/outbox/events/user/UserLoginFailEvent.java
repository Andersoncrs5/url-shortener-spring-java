package com.write.api.application.dto.outbox.events.user;

import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotBlank;

public record UserLoginFailEvent(
        @IsId
        Long id,
        @NotBlank
        String name,
        @NotBlank
        String email,
        int attemptsLoginFailed
) {
    public static UserLoginFailEvent create(Long id, String name, String email, int AttemptsLoginFailed) {
        return new UserLoginFailEvent(id, name, email, AttemptsLoginFailed);
    }
}