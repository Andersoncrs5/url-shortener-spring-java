package com.read.api.application.usecase.url;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.FindUrlByShortCodeUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FindUrlByShortCodeUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private FindUrlByShortCodeUseCaseImpl useCase;

    @Test
    void should_return_url_from_cache_when_present() {
        UrlModel url = new UrlModel();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://example.com");

        when(redis.find("url:abc123", UrlModel.class))
                .thenReturn(Optional.of(url));

        Result<UrlModel> result = useCase.execute("abc123");

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(url.getId(), result.getValue().getId());
        assertEquals(url.getShortCode(), result.getValue().getShortCode());

        verify(redis).find("url:abc123", UrlModel.class);
        verifyNoInteractions(repository);
        verify(redis, never()).save(anyString(), any(), any());
    }

    @Test
    void should_return_url_from_repository_and_save_in_cache_when_not_cached() {
        UrlModel url = new UrlModel();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://example.com");

        when(redis.find("url:abc123", UrlModel.class))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.of(url));

        Result<UrlModel> result = useCase.execute("abc123");

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(url.getId(), result.getValue().getId());
        assertEquals(url.getShortCode(), result.getValue().getShortCode());

        verify(redis).find("url:abc123", UrlModel.class);
        verify(repository).findByShortCode("abc123");
        verify(redis).save(
                eq("url:abc123"),
                eq(url),
                eq(Duration.ofMinutes(10))
        );
    }

    @Test
    void should_return_failure_when_url_not_found() {
        when(redis.find("url:abc123", UrlModel.class))
                .thenReturn(Optional.empty());

        when(repository.findByShortCode("abc123"))
                .thenReturn(Optional.empty());

        Result<UrlModel> result = useCase.execute("abc123");

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Url not found", result.getMessage());

        verify(redis).find("url:abc123", UrlModel.class);
        verify(repository).findByShortCode("abc123");
        verify(redis, never()).save(anyString(), any(), any());
    }
}