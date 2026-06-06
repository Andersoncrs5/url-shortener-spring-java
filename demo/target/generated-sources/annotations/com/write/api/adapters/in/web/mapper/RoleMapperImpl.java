package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.role.RoleDTO;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.shared.mapper.config.EnumMapper;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:15-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDTO toDTO(RoleModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        boolean active = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        name = model.getName();
        description = model.getDescription();
        active = model.isActive();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        RoleDTO roleDTO = new RoleDTO( id, name, description, active, createdAt, updatedAt );

        return roleDTO;
    }
}
