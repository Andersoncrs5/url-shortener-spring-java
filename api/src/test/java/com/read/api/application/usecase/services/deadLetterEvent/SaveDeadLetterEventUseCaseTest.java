package com.read.api.application.usecase.services.deadLetterEvent;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.deadLetterEvent.SaveDeadLetterEventUseCaseImpl;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveDeadLetterEventUseCaseTest extends BaseUseCaseTest {

    @Mock
    private DeadLetterEventRepository repository;

    @InjectMocks
    private SaveDeadLetterEventUseCaseImpl useCase;

    @Test
    void should_save_dead_letter_event_successfully_when_it_exists() {
        Long eventId = 1L;
        DeadLetterEventModel inputModel = new DeadLetterEventModel();
        inputModel.setId(eventId);
        inputModel.setPayload("{ \"data\": \"test\" }");
        inputModel.setErrorMessage("Timeout Error");

        when(repository.existsById(eventId))
                .thenReturn(true);
        when(repository.save(inputModel))
                .thenReturn(inputModel);

        Result<DeadLetterEventModel> result = useCase.execute(inputModel);

        assertTrue(result.isSuccess());
        assertEquals(201, result.getStatusCode());
        assertNotNull(result.getValue());
        assertEquals(eventId, result.getValue().getId());
        assertEquals("Timeout Error", result.getValue().getErrorMessage());

        verify(repository).existsById(eventId);
        verify(repository).save(inputModel);
    }

    @Test
    void should_return_failure_when_dead_letter_event_does_not_exist() {
        Long eventId = 2L;
        DeadLetterEventModel inputModel = new DeadLetterEventModel();
        inputModel.setId(eventId);

        when(repository.existsById(eventId))
                .thenReturn(false);

        Result<DeadLetterEventModel> result = useCase.execute(inputModel);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Dead letter event not found", result.getMessage());
        assertNull(result.getValue());

        verify(repository).existsById(eventId);
        verify(repository, never()).save(any());
    }
}