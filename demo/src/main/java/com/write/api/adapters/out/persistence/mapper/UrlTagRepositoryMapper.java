package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.generated.jooq.tables.records.UrlTagsRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlTagRepositoryMapper {

    UrlTagModel toDomain(UrlTagsRecord record);

    UrlTagsRecord toRecord(UrlTagModel model);
}
