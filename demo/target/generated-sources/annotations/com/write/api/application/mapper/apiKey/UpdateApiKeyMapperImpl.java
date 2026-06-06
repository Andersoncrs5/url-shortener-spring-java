package com.write.api.application.mapper.apiKey;

import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:15-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
)
@Component
public class UpdateApiKeyMapperImpl implements UpdateApiKeyMapper {

    @Override
    public void update(UpdateApiKeyDTO dto, ApiKeyModel entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.name() != null ) {
            entity.setName( dto.name() );
        }
        if ( dto.active() != null ) {
            entity.setActive( dto.active() );
        }
        if ( dto.expiresAt() != null ) {
            entity.setExpiresAt( dto.expiresAt() );
        }
    }
}
