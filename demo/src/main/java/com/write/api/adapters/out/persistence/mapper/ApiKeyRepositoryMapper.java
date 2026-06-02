package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.generated.jooq.tables.records.ApiKeysRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface ApiKeyRepositoryMapper {

    ApiKeyModel toDomain(ApiKeysRecord record);

    ApiKeysRecord toRecord(ApiKeyModel model);
}