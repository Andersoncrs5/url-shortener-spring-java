package com.read.api.application.usecase.url;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.FindUrlByIdUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindUrlByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private FindUrlByIdUseCaseImpl useCase;

    @Test
    void shouldReturnUrlWhenExists() {

        UrlModel url = new UrlModel();
        url.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(url));

        Result<UrlModel> result =
                useCase.execute(1L);

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());

        assertNotNull(result.getValue());
        assertEquals(1L, result.getValue().getId());
        assertEquals(200, result.getStatusCode());

        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenUrlDoesNotExist() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Result<UrlModel> result =
                useCase.execute(1L);

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());

        assertNull(result.getValue());
        assertEquals(404, result.getStatusCode());
        assertEquals(
                "Url not found",
                result.getMessage()
        );

        verify(repository).findById(1L);
    }
}