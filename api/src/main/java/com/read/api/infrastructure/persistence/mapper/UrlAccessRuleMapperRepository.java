package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.infrastructure.persistence.entity.UrlAccessRuleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlAccessRuleMapperRepository {

    UrlAccessRuleModel toModel(UrlAccessRuleEntity entity);

    UrlAccessRuleEntity toEntity(UrlAccessRuleModel model);

}
