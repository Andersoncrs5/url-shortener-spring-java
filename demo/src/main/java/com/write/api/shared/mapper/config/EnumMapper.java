package com.write.api.shared.mapper.config;

import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumMapper {

    default String map(Enum<?> value) {
        return value == null ? null : value.name();
    }

    default AggregateTypeEnum toAggregateType(String value) {
        return value == null ? null : AggregateTypeEnum.valueOf(value);
    }

    default EventTypeEnum toEventType(String value) {
        return value == null ? null : EventTypeEnum.valueOf(value);
    }

    default OutboxStatusEnum toStatus(String value) {
        return value == null ? null : OutboxStatusEnum.valueOf(value);
    }
}