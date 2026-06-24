package com.read.api.application.usecase.impl.cdc.urlAccessRule;

import com.read.api.domain.cdc.classes.UrlAccessRuleCdcEvent;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UrlAccessRuleCdcMapper {
    UrlAccessRuleModel toModel(UrlAccessRuleCdcEvent model);
}
