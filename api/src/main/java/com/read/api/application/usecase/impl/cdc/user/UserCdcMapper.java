package com.read.api.application.usecase.impl.cdc.user;

import com.read.api.domain.cdc.classes.UserCdcEvent;
import com.read.api.domain.model.UserModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface UserCdcMapper {

    @Mapping(target = "roles", ignore = true)
    UserModel toModel(UserCdcEvent model);
}
