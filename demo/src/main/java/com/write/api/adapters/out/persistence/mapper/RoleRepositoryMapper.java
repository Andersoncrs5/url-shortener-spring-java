package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.RoleModel;
import com.write.api.generated.jooq.tables.records.RolesRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface RoleRepositoryMapper {

    RoleModel toDomain(RolesRecord record);

    RolesRecord toRecord(RoleModel model);
}
