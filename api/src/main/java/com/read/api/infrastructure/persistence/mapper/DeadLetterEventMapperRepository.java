package com.read.api.infrastructure.persistence.mapper;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import com.read.api.infrastructure.persistence.entity.DeadLetterEventEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface DeadLetterEventMapperRepository {

    DeadLetterEventModel toModel(DeadLetterEventEntity entity);

    DeadLetterEventEntity toEntity(DeadLetterEventModel model);
}
