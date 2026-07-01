package com.read.api.application.usecase.mapper;

import com.read.api.domain.cdc.classes.UrlTagCdcEvent;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UrlTagServicesMapper {

    UrlTagModel toModel(UrlTagCdcEvent entity);
    UrlTagCdcEvent toCdc(UrlTagModel model);

}
