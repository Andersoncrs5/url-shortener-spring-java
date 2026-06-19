package com.read.api.api.controller.urlTag;

import com.read.api.api.dto.tag.UrlTagDTO;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UrlTagMapperController {
    UrlTagDTO toDTO(UrlTagModel model);
}
