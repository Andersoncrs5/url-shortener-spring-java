package com.write.api.application.service.outbox;

import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.in.outbox.PublishPendingOutboxEventsUseCase;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublishPendingOutboxEventsService implements PublishPendingOutboxEventsUseCase {

    IOutboxEventRepository repository;
    OutboxEventPublisher publisher;

    @Override
    @ResultTransaction
    @TrackExecutionTime("outbox.publish.pending")
    public void execute() {
        List<OutboxEventModel> events = repository.findByStatus(OutboxStatusEnum.PENDING,100);
        List<OutboxEventModel> toSave = new java.util.ArrayList<>();

        for (OutboxEventModel event : events) {
            try {
                SendResult<String, String> published = publisher.publish(event);

                event.setStatus(OutboxStatusEnum.PROCESSED);
                event.setProcessedAt(LocalDateTime.now());
                toSave.add(event);

                log.info(
                        "Outbox event {} published to topic={}, partition={}, offset={}",
                        event.getId(),
                        published.getRecordMetadata().topic(),
                        published.getRecordMetadata().partition(),
                        published.getRecordMetadata().offset()
                );

            } catch (Exception ex) {
                event.setStatus(OutboxStatusEnum.FAILED);
                event.setErrorMessage(ex.getMessage());
                event.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
                toSave.add(event);

                log.error("Failed to publish outbox event {}", event.getId(), ex);
            }
        }

        if (!toSave.isEmpty()) {
            repository.saveAll(toSave);
        }
    }
}