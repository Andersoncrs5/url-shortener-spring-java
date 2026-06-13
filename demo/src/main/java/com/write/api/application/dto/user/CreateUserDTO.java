package com.write.api.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public record CreateUserDTO(
        @Schema(
                description = "User full name",
                example = "Anderson Carvalho"
        )
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must not exceed 120 characters")
        String name,

        @Schema(
                description = "Unique email address",
                example = "anderson@email.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @Schema(
                description = "User password",
                example = "StrongPassword123!"
        )
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password
) {
}
