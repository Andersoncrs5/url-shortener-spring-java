package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.generated.jooq.tables.records.OutboxEventsRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import org.jooq.JSON;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        imports = {JSON.class}
)
public interface JooqOutboxEventRepositoryMapper {

    @Mapping(target = "aggregateType", expression = "java(enumName(model.getAggregateType()))")
    @Mapping(target = "eventType", expression = "java(enumName(model.getEventType()))")
    @Mapping(target = "status", expression = "java(enumName(model.getStatus()))")
    @Mapping(target = "payload", expression = "java(org.jooq.JSON.json(model.getPayload()))")
    OutboxEventsRecord toRecord(OutboxEventModel model);

    default String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }

    @Mapping(
            target = "payload",
            expression = "java(record.getPayload() != null ? record.getPayload().data() : null)"
    )
    @Mapping(
            target = "aggregateType",
            expression = "java(record.getAggregateType() != null ? com.write.api.core.domain.enums.AggregateTypeEnum.valueOf(record.getAggregateType()) : null)"
    )
    @Mapping(
            target = "eventType",
            expression = "java(record.getEventType() != null ? com.write.api.core.domain.enums.EventTypeEnum.valueOf(record.getEventType()) : null)"
    )
    @Mapping(
            target = "status",
            expression = "java(record.getStatus() != null ? com.write.api.core.domain.enums.OutboxStatusEnum.valueOf(record.getStatus()) : null)"
    )
    OutboxEventModel toDomain(OutboxEventsRecord record);
}