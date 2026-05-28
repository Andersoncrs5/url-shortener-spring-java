package com.write.api.application.mapper.role;

import com.write.api.application.dto.role.CreateRoleDTO;
import com.write.api.core.domain.model.RoleModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-28T14:18:24-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class CreateRoleMapperImpl implements CreateRoleMapper {

    @Override
    public RoleModel toModel(CreateRoleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        RoleModel roleModel = new RoleModel();

        roleModel.setName( dto.name() );
        roleModel.setDescription( dto.description() );
        roleModel.setActive( dto.active() );

        return roleModel;
    }
}
