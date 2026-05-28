package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.userRole.UserRoleDTO;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UserRoleMapper {

    UserRoleDTO toDTO(UserRoleModel model);

}