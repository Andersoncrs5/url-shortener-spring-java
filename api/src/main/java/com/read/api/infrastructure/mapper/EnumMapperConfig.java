package com.read.api.infrastructure.mapper;

public interface EnumMapperConfig {
    default String map(Enum<?> value) {
        return value == null ? null : value.name();
    }
}
