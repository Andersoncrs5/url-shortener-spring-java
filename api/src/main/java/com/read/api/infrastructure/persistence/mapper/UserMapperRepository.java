package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.UserModel;
import com.read.api.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperRepository {

    UserModel toModel(UserEntity entity);

    UserEntity toEntity(UserModel model);

}