package com.write.api.application.mapper.url;

import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = com.write.api.shared.mapper.config.CentralMapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UpdateUrlMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "shortCode", ignore = true)
    @Mapping(target = "customAlias", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "lastAccessAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void update(UpdateUrlDTO dto, @MappingTarget UrlModel model);
}