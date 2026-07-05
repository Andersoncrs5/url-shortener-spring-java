package com.read.api.domain.cdc.classes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.infrastructure.config.jackson.Boolean01Deserializer;
import java.time.LocalDateTime;

public record RoleCdcEvent(
        Long id,
        String name,
        String description,
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean active,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) implements BaseCdcEvent {
        public RoleCdcEvent {
                if (name == null) name = "";
                if (description == null) description = "";
                if (active == null) active = false;
        }
}