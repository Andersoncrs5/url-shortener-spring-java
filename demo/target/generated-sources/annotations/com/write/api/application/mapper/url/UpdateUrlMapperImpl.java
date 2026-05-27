package com.write.api.application.mapper.url;

import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.core.domain.model.UrlModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-26T18:11:19-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UpdateUrlMapperImpl implements UpdateUrlMapper {

    @Override
    public void update(UpdateUrlDTO dto, UrlModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.description() != null ) {
            model.setDescription( dto.description() );
        }
        if ( dto.faviconUrl() != null ) {
            model.setFaviconUrl( dto.faviconUrl() );
        }
        if ( dto.originalUrl() != null ) {
            model.setOriginalUrl( dto.originalUrl() );
        }
        if ( dto.title() != null ) {
            model.setTitle( dto.title() );
        }
        if ( dto.domain() != null ) {
            model.setDomain( dto.domain() );
        }
        if ( dto.status() != null ) {
            model.setStatus( dto.status() );
        }
        if ( dto.accessType() != null ) {
            model.setAccessType( dto.accessType() );
        }
        if ( dto.expiresAt() != null ) {
            model.setExpiresAt( dto.expiresAt() );
        }
    }
}
