package com.read.api.application.usecase.url;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.InsertUrlUseCaseImpl;
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

class InsertUrlUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private InsertUrlUseCaseImpl useCase;

    @Test
    void should_insert_url_and_cache_it() {

        UrlModel url = new UrlModel();
        url.setId(1L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://example.com");

        when(repository.insert(url))
                .thenReturn(url);

        Result<UrlModel> result = useCase.execute(url);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());

        assertEquals(url.getId(), result.getValue().getId());
        assertEquals(url.getShortCode(), result.getValue().getShortCode());
        assertEquals(url.getOriginalUrl(), result.getValue().getOriginalUrl());

        verify(repository).insert(url);

        verify(redis).save(
                eq("url:abc123"),
                eq(url),
                eq(Duration.ofMinutes(10))
        );
    }

    @Test
    void should_use_short_code_as_cache_key() {

        UrlModel url = new UrlModel();
        url.setId(10L);
        url.setShortCode("my-custom-code");

        when(repository.insert(url))
                .thenReturn(url);

        useCase.execute(url);

        verify(redis).save(
                eq("url:my-custom-code"),
                eq(url),
                eq(Duration.ofMinutes(10))
        );

        verify(repository).insert(url);
    }
}