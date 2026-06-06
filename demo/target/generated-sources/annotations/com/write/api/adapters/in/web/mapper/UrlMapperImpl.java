package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.shared.mapper.config.EnumMapper;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T14:38:01-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UrlMapperImpl implements UrlMapper {

    @Override
    public UrlResponseDTO toResponse(UrlModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        Long version = null;
        Long userId = null;
        String shortCode = null;
        String description = null;
        String faviconUrl = null;
        String originalUrl = null;
        String title = null;
        String domain = null;
        UrlStatusEnum status = null;
        UrlAccessTypeEnum accessType = null;
        boolean customAlias = false;
        LocalDateTime deletedAt = null;
        LocalDateTime expiresAt = null;
        LocalDateTime lastAccessAt = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        version = model.getVersion();
        userId = model.getUserId();
        shortCode = model.getShortCode();
        description = model.getDescription();
        faviconUrl = model.getFaviconUrl();
        originalUrl = model.getOriginalUrl();
        title = model.getTitle();
        domain = model.getDomain();
        status = model.getStatus();
        accessType = model.getAccessType();
        customAlias = model.isCustomAlias();
        deletedAt = model.getDeletedAt();
        expiresAt = model.getExpiresAt();
        lastAccessAt = model.getLastAccessAt();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        UrlResponseDTO urlResponseDTO = new UrlResponseDTO( id, version, userId, shortCode, description, faviconUrl, originalUrl, title, domain, status, accessType, customAlias, deletedAt, expiresAt, lastAccessAt, createdAt, updatedAt );

        return urlResponseDTO;
    }
}
