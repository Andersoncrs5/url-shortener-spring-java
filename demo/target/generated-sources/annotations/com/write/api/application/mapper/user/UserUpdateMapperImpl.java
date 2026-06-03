package com.write.api.application.mapper.user;

import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.dto.user.UserResponseDTO;
import com.write.api.core.domain.model.UserModel;
import java.time.LocalDateTime;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-02T20:04:24-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UserUpdateMapperImpl implements UserUpdateMapper {

    @Override
    public void updateUserFromDto(UpdateUserDTO dto, UserModel user) {
        if ( dto == null ) {
            return;
        }

        if ( dto.name() != null ) {
            user.setName( dto.name() );
        }
    }

    @Override
    public UserResponseDTO toDTO(UserModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String email = null;
        Long version = null;
        LocalDateTime createdAt = null;

        id = model.getId();
        name = model.getName();
        email = model.getEmail();
        version = model.getVersion();
        createdAt = model.getCreatedAt();

        Set<String> roles = null;

        UserResponseDTO userResponseDTO = new UserResponseDTO( id, name, email, version, createdAt, roles );

        return userResponseDTO;
    }

    @Override
    public UserModel toModel(UserResponseDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserModel userModel = new UserModel();

        userModel.setId( dto.id() );
        userModel.setVersion( dto.version() );
        userModel.setName( dto.name() );
        userModel.setEmail( dto.email() );
        userModel.setCreatedAt( dto.createdAt() );

        return userModel;
    }
}
