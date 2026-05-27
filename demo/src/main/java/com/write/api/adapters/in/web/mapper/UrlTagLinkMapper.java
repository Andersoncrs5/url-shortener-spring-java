package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlTagLinkMapper {

    UrlTagLinkDTO toDTO(UrlTagLinkModel model);

    UrlTagLinkModel toModel(UrlTagLinkDTO dto);
}