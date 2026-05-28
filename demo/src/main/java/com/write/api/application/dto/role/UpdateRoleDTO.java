package com.write.api.application.dto.role;

import jakarta.validation.constraints.Size;

public record UpdateRoleDTO(

        @Size(max = 100, message = "name exceeded 100 characters")
        String name,

        @Size(max = 5000, message = "description exceeded 5000 characters")
        String description,

        Boolean active

) {
}