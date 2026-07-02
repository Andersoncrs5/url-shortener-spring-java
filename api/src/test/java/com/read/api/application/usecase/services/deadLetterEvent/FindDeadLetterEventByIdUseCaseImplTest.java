package com.read.api.application.usecase.services.deadLetterEvent;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.deadLetterEvent.FindDeadLetterEventByIdUseCaseImpl;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindDeadLetterEventByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private DeadLetterEventRepository repository;

    @InjectMocks
    private FindDeadLetterEventByIdUseCaseImpl useCase;

    @Test
    void shouldReturnDeadLetterEventWhenExists() {

        DeadLetterEventModel event = new DeadLetterEventModel();
        event.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(event));

        Result<DeadLetterEventModel> result =
                useCase.execute(1L);

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());

        assertNotNull(result.getValue());
        assertEquals(1L, result.getValue().getId());
        assertEquals(200, result.getStatusCode());

        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeadLetterEventDoesNotExist() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Result<DeadLetterEventModel> result =
                useCase.execute(1L);

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());

        assertNull(result.getValue());
        assertEquals(404, result.getStatusCode());
        assertEquals(
                "Dead Letter Event not found",
                result.getMessage()
        );

        verify(repository).findById(1L);
    }
}