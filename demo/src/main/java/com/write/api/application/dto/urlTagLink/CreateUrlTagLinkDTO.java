package com.write.api.application.dto.urlTagLink;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUrlTagLinkDTO(

        @NotNull(message = "urlId is required")
        @Min(value = 1, message = "urlId must be greater than 0")
        Long urlId,

        @NotNull(message = "tagId is required")
        @Min(value = 1, message = "tagId must be greater than 0")
        Long tagId,

        @Min(value = 0, message = "sortOrder cannot be negative")
        @Max(value = 32767, message = "sortOrder exceeded maximum value")
        Short sortOrder,

        @Size(max = 500, message = "note exceeded 500 characters")
        String note,

        boolean primaryTag

) {
}