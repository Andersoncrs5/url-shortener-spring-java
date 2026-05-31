package com.write.api.application.mapper.role;

import com.write.api.application.dto.role.UpdateRoleDTO;
import com.write.api.core.domain.model.RoleModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T18:43:59-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UpdateRoleMapperImpl implements UpdateRoleMapper {

    @Override
    public void merge(UpdateRoleDTO dto, RoleModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.name() != null ) {
            model.setName( dto.name() );
        }
        if ( dto.description() != null ) {
            model.setDescription( dto.description() );
        }
        if ( dto.active() != null ) {
            model.setActive( dto.active() );
        }
    }
}
