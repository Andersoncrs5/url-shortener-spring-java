package com.write.api.application.mapper.urlTagLink;

import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UpdateUrlTagLinkMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "urlId", ignore = true)
    @Mapping(target = "tagId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(
            UpdateUrlTagLinkDTO dto,
            @MappingTarget UrlTagLinkModel model
    );
}