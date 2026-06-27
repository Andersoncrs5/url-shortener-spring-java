package com.read.api.application.usecase.mapper;

import com.read.api.domain.cdc.classes.UserCdcEvent;
import com.read.api.domain.model.UserModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface UserServicesMapper {

    @Mapping(target = "roles", ignore = true)
    UserModel toModel(UserCdcEvent entity);

    @Mapping(target = "roles", ignore = true)
    UserCdcEvent toCdc(UserModel model);

}
