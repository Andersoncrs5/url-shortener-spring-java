package com.write.api.shared.mapper.config;

public class JooqEnumMapper {

    public static String toDb(Enum<?> value) {
        return value == null ? null : value.name();
    }

    public static <E extends Enum<E>> E fromDb(String value, Class<E> type) {
        return value == null ? null : Enum.valueOf(type, value);
    }
}