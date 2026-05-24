package com.write.api.application.mapper.urlTag;

import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UpdateUrlTagMapper {

    void updateModelFromDto(
            UpdateUrlTagDTO dto,
            @MappingTarget UrlTagModel model
    );
}