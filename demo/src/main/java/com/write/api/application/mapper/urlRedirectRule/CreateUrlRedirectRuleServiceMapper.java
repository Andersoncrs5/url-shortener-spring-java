package com.write.api.application.mapper.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateUrlRedirectRuleServiceMapper {

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "ruleHash", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    UrlRedirectRuleModel toModel(CreateUrlRedirectRuleDTO dto);
}