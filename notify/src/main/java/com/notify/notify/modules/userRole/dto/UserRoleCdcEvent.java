package com.notify.notify.modules.userRole.dto;

import com.notify.notify.utils.base.cdc.BaseCdcEvent;
import java.time.LocalDateTime;

public record UserRoleCdcEvent(
        Long id,
        Long userId,
        Long roleId,
        Long assignedByUserId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseCdcEvent {

    public UserRoleCdcEvent {
        if (userId == null) userId = 0L;
        if (roleId == null) roleId = 0L;
        if (assignedByUserId == null) assignedByUserId = 0L;
    }
}