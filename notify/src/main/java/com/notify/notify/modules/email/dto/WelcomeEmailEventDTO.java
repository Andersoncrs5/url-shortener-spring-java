package com.notify.notify.modules.email.dto;

import jakarta.validation.constraints.NotNull;

public record WelcomeEmailEventDTO(
        Long userId,
        String email,
        String name
) {
    public static WelcomeEmailEventDTO create(
            Long userId,
            @NotNull String email,
            @NotNull String name
    ) {
        return new WelcomeEmailEventDTO(
                userId,
                email,
                name
        );
    }
}
