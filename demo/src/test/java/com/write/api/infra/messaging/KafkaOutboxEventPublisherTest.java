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
    @DisplayName("When handling data mapping edge cases")
    class DataMappingEdgeCases {

        @Test
        @DisplayName("Should throw NullPointerException if Topic is null before hitting Kafka")
        void shouldThrowNpeIfTopicIsNull() {
            validEvent.setTopic(null);

            assertThatThrownBy(() -> publisher.publish(validEvent))
                    .isInstanceOf(NullPointerException.class);

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


    }


}