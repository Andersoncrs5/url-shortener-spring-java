package com.read.api.application.usecase.impl.cdc.url;

import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.model.UrlModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UrlCdcMapper {
    UrlModel toModel(UrlCdcEvent model);
}
