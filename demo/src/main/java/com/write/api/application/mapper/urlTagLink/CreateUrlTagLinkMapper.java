package com.write.api.application.mapper.urlTagLink;

import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateUrlTagLinkMapper {

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "createdBy", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    UrlTagLinkModel toModel(CreateUrlTagLinkDTO dto);
}