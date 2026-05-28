package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlRedirectRuleMapper {

    UrlRedirectRuleDTO toDTO(UrlRedirectRuleModel model);
}