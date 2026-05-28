package com.write.api.application.mapper.role;

import com.write.api.application.dto.role.UpdateRoleDTO;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UpdateRoleMapper {

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void merge(
            UpdateRoleDTO dto,
            @MappingTarget RoleModel model
    );
}