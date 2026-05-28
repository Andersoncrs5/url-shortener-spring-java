package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.generated.jooq.tables.records.UserRolesRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UserRoleRepositoryMapper {

    UserRoleModel toDomain(UserRolesRecord record);

}