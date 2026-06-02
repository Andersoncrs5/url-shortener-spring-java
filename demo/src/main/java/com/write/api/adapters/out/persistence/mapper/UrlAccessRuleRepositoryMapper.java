package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.generated.jooq.tables.records.UrlAccessRuleRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlAccessRuleRepositoryMapper {

    @Mapping(target = "type", expression = "java(mapType(record.getType()))")
    @Mapping(target = "ruleValue", source = "ruleValue")
    UrlAccessRuleModel toDomain(UrlAccessRuleRecord record);

    @Mapping(target = "type", expression = "java(mapType(model.getType()))")
    UrlAccessRuleRecord toRecord(UrlAccessRuleModel model);

    default UrlAccessRuleTypeEnum mapType(String value) {
        return value == null ? null : UrlAccessRuleTypeEnum.valueOf(value);
    }

    default String mapType(UrlAccessRuleTypeEnum value) {
        return value == null ? null : value.name();
    }
}