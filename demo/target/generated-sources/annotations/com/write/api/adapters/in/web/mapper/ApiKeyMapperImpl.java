package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.apiKey.ApiKeyDTO;
import com.write.api.core.domain.model.ApiKeyModel;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-01T14:31:03-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class ApiKeyMapperImpl implements ApiKeyMapper {

    @Override
    public ApiKeyDTO toDTO(ApiKeyModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        boolean active = false;
        LocalDateTime lastUsedAt = null;
        LocalDateTime expiresAt = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        name = model.getName();
        active = model.isActive();
        lastUsedAt = model.getLastUsedAt();
        expiresAt = model.getExpiresAt();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        ApiKeyDTO apiKeyDTO = new ApiKeyDTO( id, name, active, lastUsedAt, expiresAt, createdAt, updatedAt );

        return apiKeyDTO;
    }
}
