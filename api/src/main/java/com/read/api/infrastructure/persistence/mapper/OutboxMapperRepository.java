package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.OutboxEventModel;
import com.read.api.infrastructure.persistence.entity.OutboxEventEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OutboxMapperRepository {

    OutboxEventModel toModel(OutboxEventEntity entity);

    OutboxEventEntity toEntity(OutboxEventModel model);

}