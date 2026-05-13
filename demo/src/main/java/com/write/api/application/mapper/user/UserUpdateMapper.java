package com.write.api.application.mapper.user;

import com.write.api.shared.mapper.config.CentralMapperConfig;
import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.core.domain.model.UserModel;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class
)
public interface UserUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    void updateUserFromDto(
            UpdateUserDTO dto,
            @MappingTarget UserModel user
    );
}