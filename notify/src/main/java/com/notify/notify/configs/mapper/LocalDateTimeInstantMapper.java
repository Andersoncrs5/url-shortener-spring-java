package com.notify.notify.configs.mapper;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class LocalDateTimeInstantMapper {

    public Instant map(LocalDateTime value) {
        return value == null
                ? null
                : value.atZone(ZoneId.systemDefault()).toInstant();
    }

    public LocalDateTime map(Instant value) {
        return value == null
                ? null
                : LocalDateTime.ofInstant(value, ZoneId.systemDefault());
    }
}