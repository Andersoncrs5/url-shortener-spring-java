package com.write.api.application.mapper.role;

import com.write.api.application.dto.role.CreateRoleDTO;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateRoleMapper {

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RoleModel toModel(CreateRoleDTO dto);
}