package com.read.api.api.controller.deadLetterEvent;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventDTO;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.infrastructure.mapper.CentralMapperConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface DeadLetterEventMapperController {
    DeadLetterEventDTO toDTO(DeadLetterEventModel model);
}
