package com.write.api.infra.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.infrastructure.messaging.kafka.KafkaOutboxEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaOutboxEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaOutboxEventPublisher publisher;

    private OutboxEventModel event;

    @BeforeEach
    void setup() {
        event = new OutboxEventModel();
        event.setId(42L);
        event.setAggregateType(AggregateTypeEnum.URL);
        event.setAggregateId(10L);
        event.setEventType(EventTypeEnum.URL_CREATED);
        event.setTopic(TopicEnum.URL_CREATED);
        event.setPayload("{\"id\":10}");
        event.setVersion(1L);
    }

    @Test
    void shouldPublishSuccessfully() throws Exception {
        String json = "{\"eventId\":\"42\"}";
        SendResult<String, String> sendResult = mock(SendResult.class);

        when(objectMapper.writeValueAsString(any(OutboxEventMessage.class)))
                .thenReturn(json);

        when(kafkaTemplate.send(
                eq("URL_CREATED"),
                eq("10"),
                eq(json)
        )).thenReturn(CompletableFuture.completedFuture(sendResult));

        SendResult<String, String> result = publisher.publish(event);

        assertThat(result).isSameAs(sendResult);

        ArgumentCaptor<OutboxEventMessage> captor =
                ArgumentCaptor.forClass(OutboxEventMessage.class);

        verify(objectMapper).writeValueAsString(captor.capture());

        OutboxEventMessage message = captor.getValue();
        assertThat(message.eventId()).isEqualTo("42");
        assertThat(message.aggregateType()).isEqualTo("URL");
        assertThat(message.aggregateId()).isEqualTo(10L);
        assertThat(message.eventType()).isEqualTo("URL_CREATED");
        assertThat(message.topic()).isEqualTo("URL_CREATED");
        assertThat(message.payload()).isEqualTo("{\"id\":10}");
        assertThat(message.version()).isEqualTo(1L);

        verify(kafkaTemplate).send("URL_CREATED", "10", json);
        verifyNoMoreInteractions(objectMapper, kafkaTemplate);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
        when(objectMapper.writeValueAsString(any(OutboxEventMessage.class)))
                .thenThrow(new JsonProcessingException("invalid json") {});

        assertThatThrownBy(() -> publisher.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to serialize event")
                .hasCauseInstanceOf(JsonProcessingException.class)
                .hasRootCauseMessage("invalid json");

        verify(objectMapper).writeValueAsString(any(OutboxEventMessage.class));
        verifyNoInteractions(kafkaTemplate);
        verifyNoMoreInteractions(objectMapper);
    }

    @Test
    void shouldWrapKafkaFailureFromFuture() throws Exception {
        String json = "{\"ok\":true}";
        CompletableFuture<SendResult<String, String>> failedFuture =
                new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("broker down"));

        when(objectMapper.writeValueAsString(any(OutboxEventMessage.class)))
                .thenReturn(json);

        when(kafkaTemplate.send("URL_CREATED", "10", json))
                .thenReturn(failedFuture);

        assertThatThrownBy(() -> publisher.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to serialize event")
                .hasCauseInstanceOf(CompletionException.class)
                .hasRootCauseMessage("broker down");

        verify(objectMapper).writeValueAsString(any(OutboxEventMessage.class));
        verify(kafkaTemplate).send("URL_CREATED", "10", json);
        verifyNoMoreInteractions(objectMapper, kafkaTemplate);
    }
}