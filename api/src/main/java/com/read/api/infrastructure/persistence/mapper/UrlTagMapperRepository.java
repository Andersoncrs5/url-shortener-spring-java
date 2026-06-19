package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.UrlTagModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import com.read.api.infrastructure.persistence.entity.UrlTagEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UrlTagMapperRepository {

    UrlTagModel toModel(UrlTagEntity entity);
    UrlTagEntity toEntity(UrlTagModel model);

}
