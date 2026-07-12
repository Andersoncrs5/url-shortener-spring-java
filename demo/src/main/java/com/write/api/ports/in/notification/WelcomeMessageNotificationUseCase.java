package com.write.api.ports.in.notification;

import com.write.api.application.dto.notification.WelcomeEmailEventDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.OutboxEventModel;

public interface WelcomeMessageNotificationUseCase {
    Result<OutboxEventModel> execute(WelcomeEmailEventDTO dto);
}
