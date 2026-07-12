package com.write.api.application.service.notification;

import com.write.api.application.dto.notification.WelcomeEmailEventDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.application.shared.annotations.UseService;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.in.notification.WelcomeMessageNotificationUseCase;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WelcomeMessageNotificationService implements WelcomeMessageNotificationUseCase {
    CreateOutboxEventUseCase create;

    @Override
    @ResultTransaction
    @TrackExecutionTime("notification.welcome.message.user")
    public Result<OutboxEventModel> execute(WelcomeEmailEventDTO dto) {

        CreateOutboxEventCommand event = new CreateOutboxEventCommand(
                AggregateTypeEnum.USER,
                dto.userId(),
                EventTypeEnum.WELCOME_EMAIL,
                TopicEnum.NOTIFY,
                dto
        );

        Result<OutboxEventModel> executed = create.execute(event);

        if (executed.isFailure()) {
            return Result.failure(executed.getErrors(), executed.getStatusCode());
        }

        return Result.success(executed.getValue());
    }

}
