package com.write.api.application.mapper.auth;

import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.core.domain.model.UserModel;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface RegisterUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "blockedAt", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "attemptsLoginFailed", constant = "0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(source = "password", target = "passwordHash")
    UserModel toDomain(CreateUserDTO dto);

}