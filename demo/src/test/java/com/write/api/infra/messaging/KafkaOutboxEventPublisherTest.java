package com.write.api.infra.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.infrastructure.messaging.kafka.KafkaOutboxEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class KafkaOutboxEventPublisherTest extends BaseServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

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

        when(kafkaTemplate.send(eq("URL_CREATED"), eq("10"), anyString()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        SendResult<String, String> result = publisher.publish(event);

        assertThat(result).isSameAs(sendResult);

        ArgumentCaptor<OutboxEventMessage> captor = ArgumentCaptor.forClass(OutboxEventMessage.class);
        verify(objectMapper).writeValueAsString(captor.capture());

        OutboxEventMessage message = captor.getValue();
        assertThat(message.eventId()).isEqualTo("42");
        assertThat(message.aggregateType()).isEqualTo("URL");
        assertThat(message.aggregateId()).isEqualTo(10L);
        assertThat(message.eventType()).isEqualTo("URL_CREATED");
        assertThat(message.topic()).isEqualTo("URL_CREATED");
        assertThat(message.payload()).isEqualTo("{\"id\":10}");
        assertThat(message.version()).isEqualTo(1L);

        verify(kafkaTemplate).send(eq("URL_CREATED"), eq("10"), anyString());
        verifyNoMoreInteractions(objectMapper, kafkaTemplate);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
        when(objectMapper.writeValueAsString(any(OutboxEventMessage.class)))
                .thenThrow(new JsonProcessingException("Serialization error") {});

        assertThatThrownBy(() -> publisher.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize event")
                .hasCauseInstanceOf(JsonProcessingException.class);

        verify(objectMapper).writeValueAsString(any(OutboxEventMessage.class));
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenKafkaDeliveryFailsAsync() throws Exception {
        String json = "{\"eventId\":\"42\"}";

        doReturn(json).when(objectMapper).writeValueAsString(any(OutboxEventMessage.class));

        CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka connection lost"));

        doReturn(failedFuture).when(kafkaTemplate).send(eq("URL_CREATED"), eq("10"), any());

        assertThatThrownBy(() -> publisher.publish(event))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kafka delivery failed")
                .hasRootCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("Kafka connection lost");

        verify(objectMapper).writeValueAsString(any(OutboxEventMessage.class));
        verify(kafkaTemplate).send(eq("URL_CREATED"), eq("10"), any());
    }

    @Test
    void shouldExecuteFallbackMethodAndThrowCircuitBreakerException() throws Exception {
        RuntimeException exceptionCause = new RuntimeException("Resilience4j triggered failure");

        java.lang.reflect.Method fallbackMethod = KafkaOutboxEventPublisher.class.getDeclaredMethod(
                "fallbackPublish", OutboxEventModel.class, Throwable.class);
        fallbackMethod.setAccessible(true);

        java.lang.reflect.InvocationTargetException reflectionException = org.junit.jupiter.api.Assertions.assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                () -> fallbackMethod.invoke(publisher, event, exceptionCause)
        );

        Throwable actualException = reflectionException.getCause();

        assertThat(actualException)
                .isInstanceOf(com.write.api.core.domain.exception.CircuitBreakerException.class)
                .hasMessageContaining("Kafka publish failed after retries: 42");

        if (actualException.getCause() != null) {
            assertThat(actualException.getCause()).isSameAs(exceptionCause);
        }
    }
}