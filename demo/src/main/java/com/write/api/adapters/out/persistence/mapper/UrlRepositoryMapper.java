package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.UrlModel;
import com.write.api.generated.jooq.tables.records.UrlsRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlRepositoryMapper {

    UrlModel toDomain(UrlsRecord record);

    UrlsRecord toRecord(UrlModel model);
}
