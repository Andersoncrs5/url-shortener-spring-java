package com.notify.notify.modules.userRole.mapper;

import com.notify.notify.configs.mapper.CentralMapperConfig;
import com.notify.notify.modules.userRole.dto.UserRoleCdcEvent;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UserRoleMapper {
    UserRoleCdcEvent toEvent(UserRoleEntity entity);

    UserRoleEntity toEntity(UserRoleCdcEvent entity);
}
