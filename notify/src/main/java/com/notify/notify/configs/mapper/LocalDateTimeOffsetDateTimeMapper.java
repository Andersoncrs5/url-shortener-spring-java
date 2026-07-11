package com.notify.notify.configs.mapper;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class LocalDateTimeOffsetDateTimeMapper {

    public OffsetDateTime map(LocalDateTime value) {
        return value == null
                ? null
                : value.atOffset(OffsetDateTime.now().getOffset());
    }

    public LocalDateTime map(OffsetDateTime value) {
        return value == null
                ? null
                : value.toLocalDateTime();
    }
}