package com.write.api.application.mapper.userRole;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface CreateUserRoleMapper {

    UserRoleModel toModel(CreateUserRoleDTO dto);

}