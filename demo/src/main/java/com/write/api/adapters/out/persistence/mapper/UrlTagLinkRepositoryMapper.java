package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.generated.jooq.tables.records.UrlTagLinksRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UrlTagLinkRepositoryMapper {

    UrlTagLinkModel toDomain(UrlTagLinksRecord record);

    UrlTagLinksRecord toRecord(UrlTagLinkModel model);
}