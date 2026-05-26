package com.write.api.application.dto.urlTagLink;

import java.time.LocalDateTime;

public record UrlTagLinkDTO(

        Long id,

        Long urlId,

        Long tagId,

        Short sortOrder,

        String note,

        boolean primaryTag,

        Long createdBy,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}