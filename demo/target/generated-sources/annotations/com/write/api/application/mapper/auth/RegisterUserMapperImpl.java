package com.write.api.application.mapper.auth;

import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.core.domain.model.UserModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-26T18:11:19-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class RegisterUserMapperImpl implements RegisterUserMapper {

    @Override
    public UserModel toDomain(CreateUserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserModel userModel = new UserModel();

        userModel.setPasswordHash( dto.password() );
        userModel.setName( dto.name() );
        userModel.setEmail( dto.email() );

        userModel.setAttemptsLoginFailed( 0 );
        userModel.setActive( true );
        userModel.setEmailVerified( false );

        return userModel;
    }
}
