package com.read.api.application.usecase.url;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.SaveUrlUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SaveUrlUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private SaveUrlUseCaseImpl useCase;

    @Test
    void should_save_url_and_cache_it() {

        UrlModel url = new UrlModel();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://example.com");

        when(repository.save(url))
                .thenReturn(url);

        Result<UrlModel> result = useCase.execute(url);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());

        assertEquals(url.getId(), result.getValue().getId());
        assertEquals(url.getShortCode(), result.getValue().getShortCode());
        assertEquals(url.getOriginalUrl(), result.getValue().getOriginalUrl());

        verify(repository).save(url);

        verify(redis).save(
                eq("url:abc123"),
                eq(url),
                eq(Duration.ofMinutes(10))
        );
    }

    @Test
    void should_return_saved_url() {

        UrlModel url = new UrlModel();
        url.setId(10L);
        url.setShortCode("github");
        url.setOriginalUrl("https://github.com");

        when(repository.save(url))
                .thenReturn(url);

        Result<UrlModel> result = useCase.execute(url);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());

        UrlModel saved = result.getValue();

        assertEquals(10L, saved.getId());
        assertEquals("github", saved.getShortCode());
        assertEquals("https://github.com", saved.getOriginalUrl());

        verify(repository).save(url);
    }

    @Test
    void should_cache_saved_url_using_correct_key() {

        UrlModel url = new UrlModel();
        url.setId(5L);
        url.setShortCode("my-code");

        when(repository.save(url))
                .thenReturn(url);

        useCase.execute(url);

        verify(redis).save(
                eq("url:my-code"),
                eq(url),
                eq(Duration.ofMinutes(10))
        );
    }
}