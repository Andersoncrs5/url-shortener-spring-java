package com.write.api.application.dto.urlTagLink;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateUrlTagLinkDTO(

        @Min(value = 0, message = "sortOrder cannot be negative")
        @Max(value = 32767, message = "sortOrder exceeded maximum ruleValue")
        Short sortOrder,

        @Size(max = 500, message = "note exceeded 500 characters")
        String note,

        Boolean primaryTag

) {
}