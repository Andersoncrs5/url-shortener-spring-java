package com.notify.notify.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.configs.kafka.dlq.DeadLetterEvent;
import com.notify.notify.configs.kafka.dlq.DeadLetterPublisherImpl;
import com.notify.notify.configs.snowflake.SnowflakeIdGenerator;
import com.notify.notify.globals.enums.TopicEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeadLetterPublisherImplTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    SnowflakeIdGenerator generator;

    @Mock
    ObjectMapper mapper;

    @InjectMocks
    DeadLetterPublisherImpl deadLetterPublisher;

    @Captor
    ArgumentCaptor<DeadLetterEvent<String>> eventCaptor;

    TopicEnum dummyTopic;
    String dummyPayload;
    Throwable dummyException;

    @BeforeEach
    void setUp() {
        dummyTopic = TopicEnum.NOTIFY_EVENT_FAILED_DLQ;
        dummyPayload = "{\"userId\": 123}";
        dummyException = new RuntimeException("Erro de conexão com banco");
    }

    @Test
    @DisplayName("Deve publicar o evento na DLQ com sucesso quando todos os dados forem válidos")
    void shouldPublishToDlqWithSuccess() throws JsonProcessingException {
        // Given (Cenário)
        long expectedEventId = 11111L;
        long expectedKafkaKey = 22222L;
        String expectedPayloadJson = "{\"id\":123}";
        String expectedFinalJson = "{\"id\":11111,\"topic\":\"NOTIFICATIONS\",\"message\":\"Erro...\"}";

        when(generator.nextId()).thenReturn(expectedEventId, expectedKafkaKey);
        when(mapper.writeValueAsString(eq(dummyPayload))).thenReturn(expectedPayloadJson);

        // Captura o objeto DeadLetterEvent enviado para o mapper na segunda conversão
        when(mapper.writeValueAsString(any(DeadLetterEvent.class))).thenReturn(expectedFinalJson);

        // When (Ação)
        assertDoesNotThrow(() -> deadLetterPublisher.publish(dummyTopic, dummyPayload, dummyException));

        // Then (Verificações)
        // 1. Verifica se converteu o payload original para string
        verify(mapper, times(1)).writeValueAsString(dummyPayload);

        verify(mapper, times(1)).writeValueAsString(eventCaptor.capture());
        DeadLetterEvent<String> capturedEvent = eventCaptor.getValue();

        assertEquals(expectedEventId, capturedEvent.id());
        assertEquals(dummyTopic.value(), capturedEvent.topic());
        assertEquals(dummyException.getMessage(), capturedEvent.error());
        assertEquals(expectedPayloadJson, capturedEvent.payload());

        // 3. Verifica o envio final para o KafkaTemplate
        verify(kafkaTemplate, times(1)).send(
                eq(dummyTopic.value()),
                eq(String.valueOf(expectedKafkaKey)),
                eq(expectedFinalJson)
        );
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando a serialização do payload original falhar")
    void shouldThrowRuntimeExceptionWhenPayloadSerializationFails() throws JsonProcessingException {
        // Given
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Erro interno do Jackson") {});

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deadLetterPublisher.publish(dummyTopic, dummyPayload, dummyException)
        );

        assertTrue(exception.getCause() instanceof JsonProcessingException);
        verifyNoInteractions(kafkaTemplate); // Garante que nada foi enviado ao Kafka
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando a serialização do DeadLetterEvent falhar")
    void shouldThrowRuntimeExceptionWhenEventSerializationFails() throws JsonProcessingException {
        // Given
        when(generator.nextId()).thenReturn(12345L);
        when(mapper.writeValueAsString(eq(dummyPayload))).thenReturn("{\"id\":123}");

        // Força erro apenas na segunda chamada do mapper (ao serializar o Event)
        when(mapper.writeValueAsString(any(DeadLetterEvent.class)))
                .thenThrow(new JsonProcessingException("Erro ao serializar o Event wrapper") {});

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                deadLetterPublisher.publish(dummyTopic, dummyPayload, dummyException)
        );

        assertTrue(exception.getCause() instanceof JsonProcessingException);
        verify(kafkaTemplate, never()).send(any(), any(), any()); // Garante que não chamou o send
    }
}