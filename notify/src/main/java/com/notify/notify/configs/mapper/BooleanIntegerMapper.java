package com.notify.notify.configs.mapper;

import org.springframework.stereotype.Component;

@Component
public class BooleanIntegerMapper {

    public boolean map(Integer value) {
        return value != null && value == 1;
    }

    public Integer map(boolean value) {
        return value ? 1 : 0;
    }
}