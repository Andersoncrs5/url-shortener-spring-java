package com.write.api.application.mapper.userRole;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:15-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
)
@Component
public class CreateUserRoleMapperImpl implements CreateUserRoleMapper {

    @Override
    public UserRoleModel toModel(CreateUserRoleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserRoleModel userRoleModel = new UserRoleModel();

        userRoleModel.setUserId( dto.userId() );
        userRoleModel.setRoleId( dto.roleId() );

        return userRoleModel;
    }
}
