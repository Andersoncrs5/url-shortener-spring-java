package com.write.api.application.mapper.urlTag;

import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateUrlTagMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UrlTagModel toModel(CreateUrlTagDTO dto);
}
