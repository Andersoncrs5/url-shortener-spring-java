package com.write.api.infra.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.exception.CircuitBreakerException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.infrastructure.messaging.kafka.KafkaOutboxEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class KafkaOutboxEventPublisherTest extends BaseServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaOutboxEventPublisher publisher;

    private OutboxEventModel validEvent;

    @BeforeEach
    void setup() {
        validEvent = new OutboxEventModel();
        validEvent.setId(42L);
        validEvent.setAggregateType(AggregateTypeEnum.URL);
        validEvent.setAggregateId(10L);
        validEvent.setEventType(EventTypeEnum.URL_CREATED);
        validEvent.setTopic(TopicEnum.URL_CREATED);
        validEvent.setPayload("{\"id\":10}");
        validEvent.setVersion(1L);
    }

    @Nested
    @DisplayName("When publishing events successfully")
    class PublishHappyPath {

        @Test
        @DisplayName("Should map and publish event to Kafka correctly")
        void shouldPublishSuccessfully() throws Exception {
            // Arrange
            String expectedJson = "{\"eventId\":\"42\"}";
            SendResult<String, String> mockSendResult = mock(SendResult.class);

            when(objectMapper.writeValueAsString(any(OutboxEventMessage.class))).thenReturn(expectedJson);
            when(kafkaTemplate.send(eq("URL_CREATED"), eq("10"), eq(expectedJson)))
                    .thenReturn(CompletableFuture.completedFuture(mockSendResult));

            // Act
            SendResult<String, String> result = publisher.publish(validEvent);

            // Assert
            assertThat(result).isSameAs(mockSendResult);

            ArgumentCaptor<OutboxEventMessage> captor = ArgumentCaptor.forClass(OutboxEventMessage.class);
            verify(objectMapper).writeValueAsString(captor.capture());

            OutboxEventMessage capturedMessage = captor.getValue();
            assertThat(capturedMessage.eventId()).isEqualTo("42");
            assertThat(capturedMessage.aggregateType()).isEqualTo("URL");
            assertThat(capturedMessage.aggregateId()).isEqualTo(10L);
            assertThat(capturedMessage.eventType()).isEqualTo("URL_CREATED");
            assertThat(capturedMessage.topic()).isEqualTo("URL_CREATED");
            assertThat(capturedMessage.payload()).isEqualTo("{\"id\":10}");
            assertThat(capturedMessage.version()).isEqualTo(1L);

            verify(kafkaTemplate).send("URL_CREATED", "10", expectedJson);
            verifyNoMoreInteractions(objectMapper, kafkaTemplate);
        }
    }

    @Nested
    @DisplayName("When handling data mapping edge cases")
    class DataMappingEdgeCases {

        @Test
        @DisplayName("Should throw NullPointerException if Topic is null before hitting Kafka")
        void shouldThrowNpeIfTopicIsNull() {
            // Arrange
            validEvent.setTopic(null); // Simulando um dado corrompido no banco

            // Act & Assert
            assertThatThrownBy(() -> publisher.publish(validEvent))
                    .isInstanceOf(NullPointerException.class);

            // Garante que nem tentou processar JSON ou ir pro Kafka
            verifyNoInteractions(objectMapper, kafkaTemplate);
        }

        @Test
        @DisplayName("Should throw NullPointerException if AggregateType is null before hitting Kafka")
        void shouldThrowNpeIfAggregateTypeIsNull() {
            // Arrange
            validEvent.setAggregateType(null);

            // Act & Assert
            assertThatThrownBy(() -> publisher.publish(validEvent))
                    .isInstanceOf(NullPointerException.class);

            verifyNoInteractions(objectMapper, kafkaTemplate);
        }
    }

    @Nested
    @DisplayName("When external dependencies fail")
    class PublishErrorScenarios {

        @Test
        @DisplayName("Should throw RuntimeException when JSON serialization fails")
        void shouldThrowRuntimeExceptionWhenSerializationFails() throws Exception {
            // Arrange
            when(objectMapper.writeValueAsString(any(OutboxEventMessage.class)))
                    .thenThrow(new JsonProcessingException("Serialization error") {});

            // Act & Assert
            assertThatThrownBy(() -> publisher.publish(validEvent))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to serialize event")
                    .hasCauseInstanceOf(JsonProcessingException.class);

            verify(objectMapper).writeValueAsString(any(OutboxEventMessage.class));
            verifyNoInteractions(kafkaTemplate);
        }

        @Test
        @DisplayName("Should throw RuntimeException when Kafka delivery fails asynchronously")
        void shouldThrowRuntimeExceptionWhenKafkaDeliveryFailsAsync() throws Exception {
            // Arrange
            String json = "{\"eventId\":\"42\"}";
            doReturn(json).when(objectMapper).writeValueAsString(any(OutboxEventMessage.class));

            CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Kafka connection lost"));

            doReturn(failedFuture).when(kafkaTemplate).send(anyString(), anyString(), anyString());

            // Act & Assert
            assertThatThrownBy(() -> publisher.publish(validEvent))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Kafka delivery failed")
                    .hasRootCauseInstanceOf(RuntimeException.class)
                    .hasRootCauseMessage("Kafka connection lost");

            verify(objectMapper).writeValueAsString(any(OutboxEventMessage.class));
            verify(kafkaTemplate).send(eq("URL_CREATED"), eq("10"), eq(json));
        }
    }

    @Nested
    @DisplayName("When Resilience4j Fallbacks are triggered")
    class ResilienceAndFallback {

        @Test
        @DisplayName("Should execute fallback method and wrap original error in CircuitBreakerException")
        void shouldExecuteFallbackMethodAndThrowCircuitBreakerException() throws Exception {
            // Arrange
            RuntimeException exceptionCause = new RuntimeException("Resilience4j triggered failure");

            Method fallbackMethod = KafkaOutboxEventPublisher.class.getDeclaredMethod(
                    "fallbackPublish", OutboxEventModel.class, Throwable.class);
            fallbackMethod.setAccessible(true);

            InvocationTargetException reflectionException = assertThrows(
                    InvocationTargetException.class,
                    () -> fallbackMethod.invoke(publisher, validEvent, exceptionCause)
            );

            Throwable actualException = reflectionException.getCause();

            assertThat(actualException)
                    .isInstanceOf(CircuitBreakerException.class)
                    .hasMessageContaining("Kafka publish failed after retries: 42");

            assertThat(actualException.getCause()).isSameAs(exceptionCause);
        }
    }
}