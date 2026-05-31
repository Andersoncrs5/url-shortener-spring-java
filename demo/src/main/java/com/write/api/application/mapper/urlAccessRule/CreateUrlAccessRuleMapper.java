package com.write.api.application.mapper.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateUrlAccessRuleMapper {

    @Mapping(target = "ruleValue", source = "ruleValue")
    UrlAccessRuleModel toDomain(CreateUrlAccessRuleDTO dto);
}
