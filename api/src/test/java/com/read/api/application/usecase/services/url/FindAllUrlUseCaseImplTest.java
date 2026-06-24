package com.read.api.application.usecase.services.url;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.FindAllUrlUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllUrlUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private FindAllUrlUseCaseImpl useCase;

    @Test
    void should_return_page_of_urls() {

        UrlModel url1 = new UrlModel();
        url1.setId(1L);
        url1.setShortCode("abc123");
        url1.setOriginalUrl("https://google.com");

        UrlModel url2 = new UrlModel();
        url2.setId(2L);
        url2.setShortCode("xyz789");
        url2.setOriginalUrl("https://github.com");

        Pageable pageable = PageRequest.of(0, 10);

        Page<UrlModel> page = new PageImpl<>(
                List.of(url1, url2),
                pageable,
                2
        );

        UrlFilter filter = new UrlFilter();

        when(repository.findAll(filter, pageable))
                .thenReturn(page);

        Page<UrlModel> result =
                useCase.execute(filter, pageable);

        assertNotNull(result);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        assertEquals("abc123",
                result.getContent().getFirst().getShortCode());

        assertEquals("xyz789",
                result.getContent().get(1).getShortCode());

        verify(repository)
                .findAll(filter, pageable);
    }

    @Test
    void should_return_empty_page_when_no_urls_found() {

        Pageable pageable = PageRequest.of(0, 10);

        UrlFilter filter = new UrlFilter();

        Page<UrlModel> page =
                Page.empty(pageable);

        when(repository.findAll(filter, pageable))
                .thenReturn(page);

        Page<UrlModel> result =
                useCase.execute(filter, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(repository)
                .findAll(filter, pageable);
    }
}