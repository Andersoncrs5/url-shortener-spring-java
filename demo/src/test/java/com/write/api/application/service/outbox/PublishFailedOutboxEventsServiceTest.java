package com.write.api.application.service.outbox;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishFailedOutboxEventsServiceTest {

    @Mock
    private IOutboxEventRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private PublishFailedOutboxEventsService service;

    private OutboxEventModel event1;
    private OutboxEventModel event2;

    @BeforeEach
    void setup() {
        event1 = new OutboxEventModel();
        event1.setId(1L);
        event1.setAggregateType(AggregateTypeEnum.ADMINS);
        event1.setAggregateId(10L);
        event1.setEventType(EventTypeEnum.ADMINS_NOTIFICATION);
        event1.setTopic(TopicEnum.NOTIFY_ADMINS);
        event1.setRetryCount(1);
        event1.setErrorMessage("Kafka unavailable");
        event1.setStatus(OutboxStatusEnum.FAILED);
        event1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        event1.setUpdatedAt(LocalDateTime.now().minusMinutes(10));

        event2 = new OutboxEventModel();
        event2.setId(2L);
        event2.setAggregateType(AggregateTypeEnum.ADMINS);
        event2.setAggregateId(11L);
        event2.setEventType(EventTypeEnum.ADMINS_NOTIFICATION);
        event2.setTopic(TopicEnum.NOTIFY_ADMINS);
        event2.setRetryCount(3);
        event2.setErrorMessage("Timeout");
        event2.setStatus(OutboxStatusEnum.FAILED);
        event2.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        event2.setUpdatedAt(LocalDateTime.now().minusMinutes(5));
    }

    @Test
    void shouldPublishFailedEventsSuccessfullyAndMarkThemAsFailedNotificationSent() {
        when(repository.findByStatus(OutboxStatusEnum.FAILED, 100))
                .thenReturn(List.of(event1, event2));

        service.execute();

        ArgumentCaptor<List<OutboxEventModel>> captor =
                ArgumentCaptor.forClass((Class) List.class);

        verify(repository).findByStatus(OutboxStatusEnum.FAILED, 100);
        verify(outbox, times(2)).execute(any(CreateOutboxEventCommand.class));
        verify(repository).saveAll(captor.capture());

        List<OutboxEventModel> saved = captor.getValue();
        assertThat(saved).hasSize(2);

        assertThat(saved.get(0).getStatus())
                .isEqualTo(OutboxStatusEnum.FAILED_NOTIFICATION_SENT);
        assertThat(saved.get(1).getStatus())
                .isEqualTo(OutboxStatusEnum.FAILED_NOTIFICATION_SENT);
    }

    @Test
    void shouldNotCallSaveAllWhenNoFailedEventsExist() {
        when(repository.findByStatus(OutboxStatusEnum.FAILED, 100))
                .thenReturn(List.of());

        service.execute();

        verify(repository).findByStatus(OutboxStatusEnum.FAILED, 100);
        verifyNoInteractions(outbox);
        verify(repository, never()).saveAll(anyList());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldPublishSingleFailedEventAndSaveIt() {
        when(repository.findByStatus(OutboxStatusEnum.FAILED, 100))
                .thenReturn(List.of(event1));

        service.execute();

        ArgumentCaptor<List<OutboxEventModel>> captor =
                ArgumentCaptor.forClass((Class) List.class);

        verify(repository).findByStatus(OutboxStatusEnum.FAILED, 100);
        verify(outbox).execute(any(CreateOutboxEventCommand.class));
        verify(repository).saveAll(captor.capture());

        List<OutboxEventModel> saved = captor.getValue();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getId()).isEqualTo(1L);
        assertThat(saved.get(0).getStatus())
                .isEqualTo(OutboxStatusEnum.FAILED_NOTIFICATION_SENT);
    }

    @Test
    void shouldBuildNotifyAdmEventCorrectly() {
        when(repository.findByStatus(OutboxStatusEnum.FAILED, 100))
                .thenReturn(List.of(event1));

        service.execute();

        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        verify(outbox).execute(captor.capture());

        CreateOutboxEventCommand command = captor.getValue();

        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.ADMINS);
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.ADMINS_NOTIFICATION);
        assertThat(command.topic()).isEqualTo(TopicEnum.NOTIFY_ADMINS);
        assertThat(command.aggregateId()).isEqualTo(event1.getId());
    }

    @Test
    void shouldPersistAllMarkedEventsOnlyOnce() {
        when(repository.findByStatus(OutboxStatusEnum.FAILED, 100))
                .thenReturn(List.of(event1, event2));

        service.execute();

        verify(repository).saveAll(argThat(list ->
                list.size() == 2
                        && list.stream().allMatch(
                        e -> e.getStatus() == OutboxStatusEnum.FAILED_NOTIFICATION_SENT
                )
        ));
    }
}