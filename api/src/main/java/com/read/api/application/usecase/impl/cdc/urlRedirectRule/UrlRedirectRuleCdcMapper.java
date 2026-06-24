package com.read.api.application.usecase.impl.cdc.urlRedirectRule;

import com.read.api.domain.cdc.classes.UrlRedirectRuleCdcEvent;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UrlRedirectRuleCdcMapper {
    UrlRedirectRuleModel toModel(UrlRedirectRuleCdcEvent model);
}
