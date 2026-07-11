package com.notify.notify.configs.mapper;

import org.springframework.stereotype.Component;

@Component
public class EnumStringMapper {

    public <E extends Enum<E>> String map(E value) {
        return value == null ? null : value.name();
    }
}
