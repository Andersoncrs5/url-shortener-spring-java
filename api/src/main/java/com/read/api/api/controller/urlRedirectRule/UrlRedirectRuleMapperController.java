package com.read.api.api.controller.urlRedirectRule;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.read.api.domain.model.UrlRedirectRuleModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface UrlRedirectRuleMapperController {
    UrlRedirectRuleDTO toDTO(UrlRedirectRuleModel model);

}
