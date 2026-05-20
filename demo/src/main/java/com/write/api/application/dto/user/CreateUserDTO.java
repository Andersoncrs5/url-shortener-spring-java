package com.write.api.application.dto.user;

public record CreateUserDTO(
        String name,
        String email,
        String password
) {
}
