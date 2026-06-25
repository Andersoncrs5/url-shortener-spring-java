package com.read.api.application.usecase.services.deadLetterEvent;

import com.read.api.application.usecase.impl.deadLetterEvent.DeleteDeadLetterEventByIdUseCaseImpl;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteDeadLetterEventByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private DeadLetterEventRepository repository;

    @InjectMocks
    private DeleteDeadLetterEventByIdUseCaseImpl useCase;

    @Test
    void should_delete_dead_letter_event_successfully() {
        Long targetId = 1L;

        when(repository.deleteById(targetId))
                .thenReturn(1);

        var result = useCase.execute(targetId);

        assertTrue(result.isSuccess());

        verify(repository).deleteById(targetId);
    }

    @Test
    void should_return_failure_when_dead_letter_event_not_found() {
        Long targetId = 1L;

        when(repository.deleteById(targetId))
                .thenReturn(0);

        var result = useCase.execute(targetId);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Dead Letter Event not found", result.getMessage());

        verify(repository).deleteById(targetId);
    }
}