package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlMapper {
    UrlResponseDTO toResponse(UrlModel model);
}
