package com.read.api.application.usecase.services.deadLetterEvent;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.deadLetterEvent.InsertDeadLetterEventUseCaseImpl;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InsertDeadLetterEventUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private DeadLetterEventRepository repository;

    @InjectMocks
    private InsertDeadLetterEventUseCaseImpl useCase;

    @Test
    void should_insert_dead_letter_event_successfully() {
        // Arrange
        DeadLetterEventModel inputModel = new DeadLetterEventModel();
        inputModel.setEventId(12345L);
        inputModel.setSourceTopic("user-topic");
        inputModel.setPayload("{ \"id\": 1 }");

        DeadLetterEventModel savedModel = new DeadLetterEventModel();
        savedModel.setId(generator.nextId());
        savedModel.setEventId(12345L);
        savedModel.setSourceTopic("user-topic");
        savedModel.setPayload("{ \"id\": 1 }");

        when(repository.insert(inputModel))
                .thenReturn(savedModel);

        // Act
        Result<DeadLetterEventModel> result = useCase.execute(inputModel);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(201, result.getStatusCode());
        assertNotNull(result.getValue());
        assertEquals(savedModel.getId(), result.getValue().getId());
        assertEquals(12345L, result.getValue().getEventId());

        verify(repository).insert(inputModel);
    }
}