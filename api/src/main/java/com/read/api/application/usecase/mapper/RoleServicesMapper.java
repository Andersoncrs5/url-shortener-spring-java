package com.read.api.application.usecase.mapper;

import com.read.api.domain.cdc.classes.RoleCdcEvent;
import com.read.api.domain.model.RoleModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface RoleServicesMapper {

    RoleModel toModel(RoleCdcEvent entity);
    RoleCdcEvent toCdc(RoleModel model);

}
