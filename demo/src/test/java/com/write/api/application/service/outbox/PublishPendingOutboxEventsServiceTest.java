package com.write.api.application.service.outbox;

import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishPendingOutboxEventsServiceTest {

    @Mock
    private IOutboxEventRepository repository;

    @Mock
    private OutboxEventPublisher publisher;

    @InjectMocks
    private PublishPendingOutboxEventsService service;

    private OutboxEventModel event1;
    private OutboxEventModel event2;

    @BeforeEach
    void setup() {
        event1 = new OutboxEventModel();
        event1.setId(1L);
        event1.setStatus(OutboxStatusEnum.PENDING);
        event1.setRetryCount(0);
        event1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        event1.setUpdatedAt(LocalDateTime.now().minusMinutes(10));

        event2 = new OutboxEventModel();
        event2.setId(2L);
        event2.setStatus(OutboxStatusEnum.PENDING);
        event2.setRetryCount(0);
        event2.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        event2.setUpdatedAt(LocalDateTime.now().minusMinutes(5));
    }

    @Test
    void shouldPublishPendingEventsSuccessfully() {
        when(repository.findByStatus(OutboxStatusEnum.PENDING, 100))
                .thenReturn(List.of(event1, event2));

        when(publisher.publish(any(OutboxEventModel.class)))
                .thenAnswer(invocation -> mockSendResult(
                        "url.created",
                        0,
                        10L
                ));

        service.execute();

        ArgumentCaptor<List<OutboxEventModel>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).findByStatus(OutboxStatusEnum.PENDING, 100);
        verify(publisher, times(2)).publish(any(OutboxEventModel.class));
        verify(repository).saveAll(captor.capture());

        List<OutboxEventModel> saved = captor.getValue();
        assertThat(saved).hasSize(2);

        assertThat(saved.get(0).getStatus()).isEqualTo(OutboxStatusEnum.PROCESSED);
        assertThat(saved.get(0).getProcessedAt()).isNotNull();

        assertThat(saved.get(1).getStatus()).isEqualTo(OutboxStatusEnum.PROCESSED);
        assertThat(saved.get(1).getProcessedAt()).isNotNull();

        InOrder order = inOrder(repository, publisher);
        order.verify(repository).findByStatus(OutboxStatusEnum.PENDING, 100);
        order.verify(publisher).publish(event1);
        order.verify(publisher).publish(event2);
        order.verify(repository).saveAll(anyList());
    }

    @Test
    void shouldMarkEventAsFailedWhenPublishThrows() {
        when(repository.findByStatus(OutboxStatusEnum.PENDING, 100))
                .thenReturn(List.of(event1));

        when(publisher.publish(event1))
                .thenThrow(new RuntimeException("Kafka unavailable"));

        service.execute();

        ArgumentCaptor<List<OutboxEventModel>> captor =
                ArgumentCaptor.forClass(List.class);

        verify(repository).findByStatus(OutboxStatusEnum.PENDING, 100);
        verify(publisher).publish(event1);
        verify(repository).saveAll(captor.capture());

        List<OutboxEventModel> saved = captor.getValue();
        assertThat(saved).hasSize(1);

        OutboxEventModel failed = saved.get(0);
        assertThat(failed.getStatus()).isEqualTo(OutboxStatusEnum.FAILED);
        assertThat(failed.getErrorMessage()).isEqualTo("Kafka unavailable");
        assertThat(failed.getNextRetryAt()).isNotNull();
        assertThat(failed.getProcessedAt()).isNull();
    }

    @Test
    void shouldNotCallSaveAllWhenNoPendingEvents() {
        when(repository.findByStatus(OutboxStatusEnum.PENDING, 100))
                .thenReturn(List.of());

        service.execute();

        verify(repository).findByStatus(OutboxStatusEnum.PENDING, 100);
        verifyNoInteractions(publisher);
        verify(repository, never()).saveAll(anyList());
    }

    private SendResult<String, String> mockSendResult(
            String topic,
            int partition,
            long offset
    ) {
        @SuppressWarnings("unchecked")
        SendResult<String, String> sendResult = mock(SendResult.class);

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition(topic, partition),
                offset,
                0,
                System.currentTimeMillis(),
                0,
                0
        );

        when(sendResult.getRecordMetadata()).thenReturn(metadata);

        return sendResult;
    }
}