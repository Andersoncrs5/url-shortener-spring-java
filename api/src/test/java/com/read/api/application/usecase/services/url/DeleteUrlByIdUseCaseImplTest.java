package com.read.api.application.usecase.services.url;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.DeleteUrlByIdUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteUrlByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private DeleteUrlByIdUseCaseImpl useCase;

    @Test
    void should_delete_url_successfully() {

        UrlModel url = new UrlModel();
        url.setId(1L);
        url.setShortCode("abc123");

        when(repository.findById(1L))
                .thenReturn(Optional.of(url));

        when(repository.deleteById(1L))
                .thenReturn(1);

        var result = useCase.execute(1L);

        assertTrue(result.isSuccess());

        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        verify(redis).delete("url:abc123");
    }

    @Test
    void should_return_failure_when_url_not_found() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        var result = useCase.execute(1L);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Url not found", result.getMessage());

        verify(repository).findById(1L);
        verify(repository, never()).deleteById(anyLong());
        verify(redis, never()).delete(anyString());
    }

    @Test
    void should_delete_cache_after_removing_url() {

        UrlModel url = new UrlModel();
        url.setId(10L);
        url.setShortCode("my-code");

        when(repository.findById(10L))
                .thenReturn(Optional.of(url));

        when(repository.deleteById(10L))
                .thenReturn(1);

        useCase.execute(10L);

        verify(redis, times(1))
                .delete("url:my-code");
    }

    @Test
    void should_find_url_before_delete() {

        UrlModel url = new UrlModel();
        url.setId(5L);
        url.setShortCode("code");

        when(repository.findById(5L))
                .thenReturn(Optional.of(url));

        when(repository.deleteById(5L))
                .thenReturn(1);

        useCase.execute(5L);

        verify(repository).findById(5L);

        verify(repository).deleteById(5L);
    }
}