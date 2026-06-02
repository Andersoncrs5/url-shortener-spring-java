package com.write.api.application.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOutboxEventServiceTest {

    @Mock
    private IOutboxEventRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CreateOutboxEventService service;

    private CreateOutboxEventCommand command;

    @BeforeEach
    void setup() {
        command = new CreateOutboxEventCommand(
                AggregateTypeEnum.USER,
                1L,
                EventTypeEnum.USER_CREATED,
                TopicEnum.USER_CREATED,
                new UserCreatedPayload(1L, "john", "john@test.com")
        );
    }

    @Test
    void shouldCreateOutboxEventSuccessfully() throws Exception {
        String json = """
                {"id":1,"name":"john","email":"john@test.com"}
                """;

        when(objectMapper.writeValueAsString(command.payload()))
                .thenReturn(json);

        when(repository.insert(any(OutboxEventModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<OutboxEventModel> result = service.execute(command);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();

        ArgumentCaptor<OutboxEventModel> captor =
                ArgumentCaptor.forClass(OutboxEventModel.class);

        verify(repository).insert(captor.capture());

        OutboxEventModel model = captor.getValue();
        assertThat(model.getAggregateType()).isEqualTo(AggregateTypeEnum.USER);
        assertThat(model.getAggregateId()).isEqualTo(1L);
        assertThat(model.getEventType()).isEqualTo(EventTypeEnum.USER_CREATED);
        assertThat(model.getTopic()).isEqualTo(TopicEnum.USER_CREATED);
        assertThat(model.getPayload()).isEqualTo(json);
        assertThat(model.getStatus()).isEqualTo(OutboxStatusEnum.PENDING);
        assertThat(model.getRetryCount()).isEqualTo(0);

        verifyNoMoreInteractions(repository, objectMapper);
    }

    @Test
    void shouldReturnFailureWhenPayloadSerializationFails() throws Exception {
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("serialization failed") {});

        Result<OutboxEventModel> result = service.execute(command);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getErrors()).containsExactly("Failed to serialize outbox payload");
        assertThat(result.getValue()).isNull();

        verify(objectMapper).writeValueAsString(command.payload());
        verifyNoInteractions(repository);
        verifyNoMoreInteractions(objectMapper);
    }

    private record UserCreatedPayload(
            Long id,
            String name,
            String email
    ) {}
}