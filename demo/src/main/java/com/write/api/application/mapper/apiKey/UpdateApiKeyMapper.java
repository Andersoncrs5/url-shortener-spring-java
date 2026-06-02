package com.write.api.application.mapper.apiKey;

import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UpdateApiKeyMapper {

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void update(
            UpdateApiKeyDTO dto,
            @MappingTarget ApiKeyModel entity
    );
}