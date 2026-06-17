package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.infrastructure.persistence.entity.UrlRedirectRuleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlRedirectRuleMapperRepository {

    UrlRedirectRuleModel toModel(UrlRedirectRuleEntity entity);

    UrlRedirectRuleEntity toEntity(UrlRedirectRuleModel model);
}
