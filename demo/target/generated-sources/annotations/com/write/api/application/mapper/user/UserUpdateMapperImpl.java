package com.write.api.application.mapper.user;

import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.core.domain.model.UserModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-20T09:04:47-0300",
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
        if ( dto.email() != null ) {
            user.setEmail( dto.email() );
        }
    }
}
