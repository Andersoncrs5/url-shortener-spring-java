package com.notify.notify.configs.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.MapperConfig(
        componentModel = "spring",
        uses = {
                MapperConfig.class,
                BooleanIntegerMapper.class,
                BooleanStringMapper.class,
                EnumStringMapper.class,
                LocalDateTimeInstantMapper.class,
                LocalDateTimeOffsetDateTimeMapper.class,
                StringEnumMapper.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public class CentralMapperConfig {
}
