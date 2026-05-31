package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlAccessRule.UrlAccessRuleResponseDTO;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlAccessRuleMapper {

    UrlAccessRuleResponseDTO toDTO(UrlAccessRuleModel model);
}