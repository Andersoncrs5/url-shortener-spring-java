package com.write.api.application.mapper.apiKey;

import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T14:38:01-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class CreateApiKeyMapperImpl implements CreateApiKeyMapper {

    @Override
    public ApiKeyModel toDomain(CreateApiKeyDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ApiKeyModel apiKeyModel = new ApiKeyModel();

        apiKeyModel.setOwnerUserId( dto.ownerUserId() );
        apiKeyModel.setName( dto.name() );
        apiKeyModel.setExpiresAt( dto.expiresAt() );

        apiKeyModel.setActive( true );

        return apiKeyModel;
    }
}
