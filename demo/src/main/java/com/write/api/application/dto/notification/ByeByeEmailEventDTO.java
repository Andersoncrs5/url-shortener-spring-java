package com.write.api.application.dto.notification;

import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotNull;

public record ByeByeEmailEventDTO(
        @IsId Long userId,
        String email,
        String name
) {
    public static ByeByeEmailEventDTO create(
            @IsId Long userId,
            @NotNull String email,
            @NotNull String name
    ) {
        return new ByeByeEmailEventDTO(
                userId,
                email,
                name
        );
    }
}

