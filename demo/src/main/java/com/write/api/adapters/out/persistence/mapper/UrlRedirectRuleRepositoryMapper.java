package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.generated.jooq.enums.UrlRedirectRulesMatchType;
import com.write.api.generated.jooq.tables.records.UrlRedirectRulesRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlRedirectRuleRepositoryMapper {

    UrlRedirectRuleModel toDomain(UrlRedirectRulesRecord record);

    UrlRedirectRulesRecord toRecord(UrlRedirectRuleModel model);

    @ValueMappings({
            @ValueMapping(source = "STARTS_WITH", target = "STARTS_WITH"),
            @ValueMapping(source = "ENDS_WITH", target = "ENDS_WITH"),
            @ValueMapping(source = "CONTAINS", target = "CONTAINS"),
            @ValueMapping(source = "ANY", target = "ANY"),
            @ValueMapping(source = "NOT", target = "NOT"),
            @ValueMapping(source = "WILDCARD", target = "ANY"),
            @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "EXACT")
    })
    UrlRedirectRulesMatchType toRecordMatchType(MatchTypeEnum value);

    @ValueMappings({
            @ValueMapping(source = "STARTS_WITH", target = "STARTS_WITH"),
            @ValueMapping(source = "ENDS_WITH", target = "ENDS_WITH"),
            @ValueMapping(source = "CONTAINS", target = "CONTAINS"),
            @ValueMapping(source = "ANY", target = "ANY"),
            @ValueMapping(source = "NOT", target = "NOT"),
            @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "EXACT")
    })
    MatchTypeEnum toDomainMatchType(UrlRedirectRulesMatchType value);
}