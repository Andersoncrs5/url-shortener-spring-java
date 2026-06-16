package com.read.api.api.controller.urlAccessRule;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.domain.model.UrlAccessRuleModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlAccessRuleMapperController {
    UrlAccessRuleDTO toDTO(UrlAccessRuleModel model);
}
