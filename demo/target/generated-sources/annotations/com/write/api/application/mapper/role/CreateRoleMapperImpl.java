package com.write.api.application.mapper.role;

import com.write.api.application.dto.role.CreateRoleDTO;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:15-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
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
