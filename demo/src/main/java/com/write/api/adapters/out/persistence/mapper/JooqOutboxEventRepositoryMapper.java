package com.write.api.adapters.out.persistence.mapper;

import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.generated.jooq.tables.records.OutboxEventsRecord;
import com.write.api.shared.mapper.config.CentralMapperConfig;
import com.write.api.shared.mapper.config.EnumMapper;
import com.write.api.shared.mapper.config.JooqEnumMapper;
import org.jooq.JSON;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = CentralMapperConfig.class,
        uses = EnumMapper.class,

        imports = {JSON.class, JooqEnumMapper.class}
)
public interface JooqOutboxEventRepositoryMapper {

    @Mapping(target = "aggregateType", expression = "java(JooqEnumMapper.toDb(model.getAggregateType()))")
    @Mapping(target = "eventType", expression = "java(JooqEnumMapper.toDb(model.getEventType()))")
    @Mapping(target = "status", expression = "java(JooqEnumMapper.toDb(model.getStatus()))")
    @Mapping(target = "payload", expression = "java(JSON.json(model.getPayload()))")
    OutboxEventsRecord toRecord(OutboxEventModel model);

    @Mapping(target = "payload",
            expression = "java(record.getPayload() != null ? record.getPayload().data() : null)"
    )
    @Mapping(target = "aggregateType",
            expression = "java(JooqEnumMapper.fromDb(record.getAggregateType(), com.write.api.core.domain.enums.AggregateTypeEnum.class))"
    )
    @Mapping(target = "eventType",
            expression = "java(JooqEnumMapper.fromDb(record.getEventType(), com.write.api.core.domain.enums.EventTypeEnum.class))"
    )
    @Mapping(target = "status",
            expression = "java(JooqEnumMapper.fromDb(record.getStatus(), com.write.api.core.domain.enums.OutboxStatusEnum.class))"
    )
    OutboxEventModel toDomain(OutboxEventsRecord record);
}