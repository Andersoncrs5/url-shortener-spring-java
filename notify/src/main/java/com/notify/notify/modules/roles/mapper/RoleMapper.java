package com.notify.notify.modules.roles.mapper;

import com.notify.notify.configs.mapper.CentralMapperConfig;
import com.notify.notify.modules.roles.dto.RoleCdcEvent;
import com.notify.notify.modules.roles.entities.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface RoleMapper {

    RoleEntity toEntity(RoleCdcEvent roleCdcEvent);

}
