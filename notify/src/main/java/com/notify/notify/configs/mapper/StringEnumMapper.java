package com.notify.notify.configs.mapper;

import org.springframework.stereotype.Component;

@Component
public class StringEnumMapper {

    public <E extends Enum<E>> E map(String value, Class<E> enumClass) {
        return value == null ? null : Enum.valueOf(enumClass, value);
    }
}
