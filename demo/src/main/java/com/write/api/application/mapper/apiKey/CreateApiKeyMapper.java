package com.write.api.application.mapper.apiKey;

import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateApiKeyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "keyHash", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "lastUsedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApiKeyModel toDomain(CreateApiKeyDTO dto);
}