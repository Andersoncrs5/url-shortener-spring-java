package com.write.api.application.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleDTO(

        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name exceeded 100 characters")
        String name,

        @NotBlank(message = "description is required")
        @Size(max = 5000, message = "description exceeded 5000 characters")
        String description,

        boolean active

) {
}