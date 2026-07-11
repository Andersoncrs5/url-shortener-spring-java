package com.notify.notify.modules.user.mapper;

import com.notify.notify.configs.mapper.CentralMapperConfig;
import com.notify.notify.modules.user.dto.UserCdcEvent;
import com.notify.notify.modules.user.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    UserCdcEvent toEvent(UserEntity entity);

    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(UserCdcEvent entity);
}
