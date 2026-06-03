package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.userRole.UserRoleDTO;
import com.write.api.core.domain.model.UserRoleModel;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-02T20:04:24-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UserRoleMapperImpl implements UserRoleMapper {

    @Override
    public UserRoleDTO toDTO(UserRoleModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        Long userId = null;
        Long roleId = null;
        Long assignedByUserId = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        userId = model.getUserId();
        roleId = model.getRoleId();
        assignedByUserId = model.getAssignedByUserId();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        UserRoleDTO userRoleDTO = new UserRoleDTO( id, userId, roleId, assignedByUserId, createdAt, updatedAt );

        return userRoleDTO;
    }
}
