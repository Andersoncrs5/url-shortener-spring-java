package com.read.api.application.usecase.services.user;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.ExistsUserByNameUseCaseImpl;
import com.read.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExistsUserByNameUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private ExistsUserByNameUseCaseImpl useCase;

    @Test
    void shouldReturnTrueWhenUserNameExists() {

        String name = "pochita";

        when(
                repository.existsByNameIgnoreCase(name)
        ).thenReturn(true);

        boolean result = useCase.execute(name);

        assertTrue(result);

        verify(repository)
                .existsByNameIgnoreCase(name);
    }

    @Test
    void shouldReturnFalseWhenUserNameDoesNotExist() {

        String name = "unknown-user";

        when(
                repository.existsByNameIgnoreCase(name)
        ).thenReturn(false);

        boolean result = useCase.execute(name);

        assertFalse(result);

        verify(repository)
                .existsByNameIgnoreCase(name);
    }

    @Test
    void shouldIgnoreNameCase() {

        String name = "POCHITA";

        when(
                repository.existsByNameIgnoreCase(name)
        ).thenReturn(true);

        boolean result = useCase.execute(name);

        assertTrue(result);

        verify(repository)
                .existsByNameIgnoreCase(name);
    }
}