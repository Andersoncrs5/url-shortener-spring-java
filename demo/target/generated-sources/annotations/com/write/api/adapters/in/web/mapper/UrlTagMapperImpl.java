package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.core.domain.model.UrlTagModel;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-04T09:04:20-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UrlTagMapperImpl implements UrlTagMapper {

    @Override
    public UrlTagResponseDTO toResponse(UrlTagModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        Long userId = null;
        String name = null;
        String slug = null;
        String color = null;
        String description = null;
        Long parentId = null;
        boolean active = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        userId = model.getUserId();
        name = model.getName();
        slug = model.getSlug();
        color = model.getColor();
        description = model.getDescription();
        parentId = model.getParentId();
        active = model.isActive();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        UrlTagResponseDTO urlTagResponseDTO = new UrlTagResponseDTO( id, userId, name, slug, color, description, parentId, active, createdAt, updatedAt );

        return urlTagResponseDTO;
    }
}
