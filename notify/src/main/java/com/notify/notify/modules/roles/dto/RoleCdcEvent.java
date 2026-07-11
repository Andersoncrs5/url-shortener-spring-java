package com.notify.notify.modules.roles.dto;

import com.notify.notify.utils.base.cdc.BaseCdcEvent;

import java.time.LocalDateTime;

public record RoleCdcEvent(
        Long id,
        String name,
        String description,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseCdcEvent {
    public RoleCdcEvent {
        if (name == null) name = "";
        if (description == null) description = "";
        if (active == null) active = false;
    }
}
