package com.write.api.adapters.out.persistence.mapper;

import com.write.api.shared.mapper.config.CentralMapperConfig;
import com.write.api.core.domain.model.UserModel;
import com.write.api.generated.jooq.tables.records.UsersRecord;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UserRepositoryMapper {

    UserModel toDomain(UsersRecord record);

    UsersRecord toRecord(UserModel model);
}