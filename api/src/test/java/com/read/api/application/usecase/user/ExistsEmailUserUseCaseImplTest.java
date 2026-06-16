package com.read.api.application.usecase.user;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.ExistsEmailUserUseCaseImpl;
import com.read.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExistsEmailUserUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private ExistsEmailUserUseCaseImpl useCase;

    @Test
    void shouldReturnTrueWhenEmailExists() {

        String email = "user@gmail.com";

        when(
                repository.existsByEmailIgnoreCase(email)
        ).thenReturn(true);

        boolean result = useCase.execute(email);

        assertTrue(result);

        verify(repository)
                .existsByEmailIgnoreCase(email);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {

        String email = "notfound@gmail.com";

        when(
                repository.existsByEmailIgnoreCase(email)
        ).thenReturn(false);

        boolean result = useCase.execute(email);

        assertFalse(result);

        verify(repository)
                .existsByEmailIgnoreCase(email);
    }

    @Test
    void shouldIgnoreEmailCase() {

        String email = "USER@GMAIL.COM";

        when(
                repository.existsByEmailIgnoreCase(email)
        ).thenReturn(true);

        boolean result = useCase.execute(email);

        assertTrue(result);

        verify(repository)
                .existsByEmailIgnoreCase(email);
    }
}