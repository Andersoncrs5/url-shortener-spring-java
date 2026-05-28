package com.write.api.application.mapper.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UpdateUrlRedirectRuleServiceMapper {

    void update(
            UpdateUrlRedirectRuleDTO dto,
            @MappingTarget UrlRedirectRuleModel model
    );
}