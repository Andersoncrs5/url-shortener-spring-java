package com.write.api.application.mapper.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateUrlMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "userId", ignore = true)

    @Mapping(target = "faviconUrl", source = "faviconUrl")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "originalUrl", source = "originalUrl")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "domain", source = "domain")
    @Mapping(target = "accessType", source = "accessType")

    @Mapping(target = "passwordHash", ignore = true)

    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "lastAccessAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    UrlModel toModel(CreateUrlDTO dto);
}