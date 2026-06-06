package com.write.api.application.service.outbox;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.admin.NotifyAdmEvent;
import com.write.api.application.dto.outbox.events.user.UserCreatedEvent;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.outbox.PublishFailedOutboxEventsUseCase;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublishFailedOutboxEventsService implements PublishFailedOutboxEventsUseCase {
    IOutboxEventRepository repository;
    CreateOutboxEventUseCase outbox;

    @Override
    public void execute() {
        List<OutboxEventModel> events = repository.findByStatus(OutboxStatusEnum.FAILED,100);
        List<OutboxEventModel> toSave = new java.util.ArrayList<>();

        for (OutboxEventModel event : events) {

            outbox.execute(
                    new CreateOutboxEventCommand(
                            AggregateTypeEnum.ADMINS,
                            event.getId(),
                            EventTypeEnum.ADMINS_NOTIFICATION,
                            TopicEnum.NOTIFY_ADMINS,
                            NotifyAdmEvent.create(
                                    event.getId(),
                                    event.getAggregateType().name(),
                                    event.getEventType().name(),
                                    event.getTopic().name(),
                                    event.getRetryCount(),
                                    event.getErrorMessage(),
                                    event.getUpdatedAt()
                            )
                    )
            );

            event.setStatus(OutboxStatusEnum.FAILED_NOTIFICATION_SENT);
            toSave.add(event);
        }

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
    }

}
