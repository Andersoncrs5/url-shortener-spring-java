package com.read.api.api.controller.urlAccessRule;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UrlAccessRuleMapperController {
    UrlAccessRuleDTO toDTO(UrlAccessRuleModel model);
}
