package com.write.api.application.mapper.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.core.domain.model.UrlModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-26T18:11:19-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class CreateUrlMapperImpl implements CreateUrlMapper {

    @Override
    public UrlModel toModel(CreateUrlDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UrlModel urlModel = new UrlModel();

        urlModel.setFaviconUrl( dto.faviconUrl() );
        urlModel.setDescription( dto.description() );
        urlModel.setOriginalUrl( dto.originalUrl() );
        urlModel.setTitle( dto.title() );
        urlModel.setDomain( dto.domain() );
        urlModel.setAccessType( dto.accessType() );
        urlModel.setExpiresAt( dto.expiresAt() );

        return urlModel;
    }
}
