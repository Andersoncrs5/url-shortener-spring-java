package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.RoleModel;
import com.read.api.infrastructure.persistence.entity.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapperRepository {

    RoleModel toModel(RoleEntity entity);

    RoleEntity toEntity(RoleModel model);

}