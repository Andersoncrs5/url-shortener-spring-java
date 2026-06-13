package com.write.api.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User authentication request")
public record LoginUserDTO(

        @Schema(
                description = "User email address",
                example = "john.doe@email.com"
        )
        @NotBlank(message = "Email is required")
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
