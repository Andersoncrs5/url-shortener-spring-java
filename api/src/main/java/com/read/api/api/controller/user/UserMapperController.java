package com.read.api.api.controller.user;

import com.read.api.api.dto.user.UserDTO;
import com.read.api.domain.model.UserModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperController {

    UserDTO toDTO(UserModel model);
}