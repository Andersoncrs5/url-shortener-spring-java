package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlTagMapper {

    UrlTagResponseDTO toResponse(UrlTagModel model);
}