package com.notify.notify.configs.mapper;

import org.springframework.stereotype.Component;

@Component
public class BooleanStringMapper {

    public boolean map(String value) {
        return Boolean.parseBoolean(value);
    }

    public String map(boolean value) {
        return Boolean.toString(value);
    }
}
