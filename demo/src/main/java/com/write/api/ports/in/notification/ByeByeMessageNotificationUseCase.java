package com.write.api.ports.in.notification;

import com.write.api.application.dto.notification.ByeByeEmailEventDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.OutboxEventModel;

public interface ByeByeMessageNotificationUseCase {
    Result<OutboxEventModel> execute(ByeByeEmailEventDTO dto);
}
