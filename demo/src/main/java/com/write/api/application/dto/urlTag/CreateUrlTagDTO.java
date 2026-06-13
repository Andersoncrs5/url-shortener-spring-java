package com.write.api.application.dto.urlTag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUrlTagDTO(
        @NotBlank(message = "name is required")
        @Size(
                max = 120,
                message = "name exceeded 120 characters"
        )
        @Schema(description = "Tag name", example = "marketing")
        String name,

        @NotBlank(message = "slug is required")
        @Size(
                max = 140,
                message = "slug exceeded 140 characters"
        )
        @Pattern(
                regexp = "^[a-z0-9-]+$",
                message = "slug must contain only lowercase letters, numbers and hyphens"
        )
        @Schema(description = "Tag slug", example = "marketing")
        String slug,

        @Size(
                max = 20,
                message = "color exceeded 20 characters"
        )
        @Pattern(
                regexp = "^#?[0-9A-Fa-f]{6}$",
                message = "color must be a valid hex color"
        )
        String color,

        @Size(
                max = 255,
                message = "description exceeded 255 characters"
        )
        String description,

        @Schema(description = "Optional parent tag id", example = "918273645")
        Long parentId,

        boolean active
) {
}
