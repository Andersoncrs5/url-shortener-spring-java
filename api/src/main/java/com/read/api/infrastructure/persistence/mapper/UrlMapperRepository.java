package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.UrlModel;
import com.read.api.infrastructure.persistence.entity.UrlEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UrlMapperRepository {

    UrlModel toModel(UrlEntity entity);

    UrlEntity toEntity(UrlModel model);

}