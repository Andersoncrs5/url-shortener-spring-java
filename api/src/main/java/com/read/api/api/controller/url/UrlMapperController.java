package com.read.api.api.controller.url;

import com.read.api.api.dto.url.UrlDTO;
import com.read.api.domain.model.UrlModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UrlMapperController {
    UrlDTO toDTO(UrlModel model);
}
