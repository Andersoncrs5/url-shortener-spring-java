package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.apiKey.ApiKeyDTO;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface ApiKeyMapper {

    ApiKeyDTO toDTO(ApiKeyModel model);
}