package com.write.api.application.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.dto.outbox.events.url.UrlCreatedEvent;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.infrastructure.config.cache.RedisCrudService;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerDlqServiceTest {

    @Mock
    private IOutboxEventRepository repository;

    @Mock
    private OutboxEventPublisher publisher;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisCrudService redisCrudService;

    @InjectMocks
    private HandlerDlqService service;

    private String payload;
    private OutboxEventMessage<String> message;
    private OutboxEventModel outboxEvent;

    @BeforeEach
    void setup() {

        payload = """
                {
                  "eventId":"1"
                }
                """;

        message = new OutboxEventMessage<>(
                "1",
                "URL",
                10L,
                "URL_CREATED",
                "url.created",
                "{}",
                1L
        );

        outboxEvent = new OutboxEventModel();

        outboxEvent.setId(1L);
        outboxEvent.setAggregateId(10L);
        outboxEvent.setRetryCount(0);
        outboxEvent.setStatus(OutboxStatusEnum.PENDING);
    }

    @Test
    void shouldSaveBeforePublishing() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                anyString(),
                anyString(),
                any(Duration.class)
        )).thenReturn(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(outboxEvent));

        service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        InOrder inOrder =
                inOrder(repository, publisher);

        inOrder.verify(repository).save(outboxEvent);
        inOrder.verify(publisher).publish(outboxEvent);
    }

    @Test
    void shouldUseExpectedRedisKey() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                anyString(),
                anyString(),
                any(Duration.class)
        )).thenReturn(false);

        service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        verify(redisCrudService).saveIfAbsent(
                eq("dlq:1"),
                eq("processed"),
                eq(Duration.ofHours(24))
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldFailWhenEventNotFoundInOutbox() throws Exception {
        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                eq("dlq:1"),
                eq("processed"),
                any(Duration.class)
        )).thenReturn(true);

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Result<Void> result = service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors().getFirst()).isEqualTo("Event not found in outbox");
        assertThat(result.getValue()).isNull();

        verify(objectMapper).readValue(eq(payload), any(TypeReference.class));
        verify(repository).findById(1L);
        verify(repository, never()).save(any());
        verifyNoInteractions(publisher);

        verifyNoMoreInteractions(objectMapper, repository);
    }

    @Test
    void shouldMarkAsFailedWhenRetryLimitExceeded() throws Exception {

        outboxEvent.setRetryCount(20);

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                anyString(),
                anyString(),
                any(Duration.class)
        )).thenReturn(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(outboxEvent));

        Result<Void> result = service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);

        assertThat(outboxEvent.getStatus())
                .isEqualTo(OutboxStatusEnum.FAILED);

        assertThat(outboxEvent.getErrorMessage())
                .isEqualTo("Max retries exceeded");

        verify(repository).save(outboxEvent);
        verifyNoInteractions(publisher);
    }

    @Test
    void shouldThrowInternalServerErrorWhenPayloadIsInvalid() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenThrow(new JsonProcessingException("invalid") {});

        assertThatThrownBy(() ->
                service.execute(
                        payload,
                        new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
                )
        )
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessageContaining("invalid");

        verifyNoInteractions(repository);
        verifyNoInteractions(publisher);
    }

    @Test
    void shouldIncrementRetryCounter() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                anyString(),
                anyString(),
                any(Duration.class)
        )).thenReturn(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(outboxEvent));

        service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        assertThat(outboxEvent.getRetryCount()).isEqualTo(1);
    }

    @Test
    void shouldRequeueEventSuccessfully() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                eq("dlq:1"),
                eq("processed"),
                any(Duration.class)
        )).thenReturn(true);

        when(repository.findById(1L))
                .thenReturn(Optional.of(outboxEvent));

        Result<Void> result = service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        assertThat(result.isSuccess()).isTrue();

        verify(repository).save(outboxEvent);
        verify(publisher).publish(outboxEvent);

        assertThat(outboxEvent.getRetryCount()).isEqualTo(1);
        assertThat(outboxEvent.getStatus())
                .isEqualTo(OutboxStatusEnum.RETRYING);
    }

    @Test
    void shouldIgnoreAlreadyProcessedEvent() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                anyString(),
                anyString(),
                any(Duration.class)
        )).thenReturn(false);

        Result<Void> result = service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        assertThat(result.isSuccess()).isTrue();

        verifyNoInteractions(repository);
        verifyNoInteractions(publisher);
    }

    @Test
    void shouldReturn404WhenOutboxEventNotFound() throws Exception {

        when(objectMapper.readValue(eq(payload), any(TypeReference.class)))
                .thenReturn(message);

        when(redisCrudService.saveIfAbsent(
                anyString(),
                anyString(),
                any(Duration.class)
        )).thenReturn(true);

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Result<Void> result = service.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlCreatedEvent>>() {}
        );

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);

        verifyNoInteractions(publisher);
    }
}