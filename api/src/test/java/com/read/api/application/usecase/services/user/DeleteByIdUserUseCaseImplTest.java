package com.read.api.application.usecase.services.user;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.DeleteByIdUserUseCaseImpl;
import com.read.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteByIdUserUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private DeleteByIdUserUseCaseImpl useCase;

    @Test
    void should_delete_user_successfully() {

        when(repository.deleteById(1L))
                .thenReturn(1);

        var result = useCase.execute(1L);

        assertTrue(result.isSuccess());

        verify(repository).deleteById(1L);
    }

    @Test
    void should_return_failure_when_user_not_found() {

        when(repository.deleteById(1L)).thenReturn(0);

        var result = useCase.execute(1L);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("User not found", result.getMessage());

        verify(repository).deleteById(1L);
    }
}