package com.read.api.application.usecase.services.urlTag;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.DeleteUrlTagByIdUseCaseImpl;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteUrlTagByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private DeleteUrlTagByIdUseCaseImpl useCase;

    @Test
    void should_delete_url_tag_successfully() {

        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(1);

        Result<Void> result =
                useCase.execute(id);

        assertTrue(result.isSuccess());
        assertEquals(
                200,
                result.getStatusCode()
        );

        assertNull(
                result.getValue()
        );

        verify(repository)
                .deleteById(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_not_found_when_url_tag_does_not_exist() {

        Long id = 999L;

        when(repository.deleteById(id))
                .thenReturn(0);

        Result<Void> result =
                useCase.execute(id);

        assertFalse(result.isSuccess());

        assertEquals(
                404,
                result.getStatusCode()
        );

        assertEquals(
                "Url tag not found",
                result.getMessage()
        );

        verify(repository)
                .deleteById(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_call_repository_only_once() {

        Long id = 10L;

        when(repository.deleteById(id))
                .thenReturn(1);

        useCase.execute(id);

        verify(repository, times(1))
                .deleteById(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_failure_when_repository_returns_negative_value() {

        Long id = 20L;

        when(repository.deleteById(id))
                .thenReturn(-1);

        Result<Void> result =
                useCase.execute(id);

        assertFalse(result.isSuccess());

        assertEquals(
                404,
                result.getStatusCode()
        );

        assertEquals(
                "Url tag not found",
                result.getMessage()
        );

        verify(repository)
                .deleteById(id);
    }
}