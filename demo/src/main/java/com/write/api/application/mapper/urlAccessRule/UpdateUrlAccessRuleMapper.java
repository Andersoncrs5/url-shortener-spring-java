package com.write.api.application.mapper.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UpdateUrlAccessRuleMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(
            UpdateUrlAccessRuleDTO dto,
            @MappingTarget UrlAccessRuleModel model
    );
}