package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.role.RoleDTO;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface RoleMapper {

    RoleDTO toDTO(RoleModel model);
}