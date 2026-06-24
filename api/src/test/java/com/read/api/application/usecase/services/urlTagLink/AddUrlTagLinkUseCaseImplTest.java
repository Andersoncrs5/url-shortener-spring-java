package com.read.api.application.usecase.services.urlTagLink;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTagLink.AddUrlTagLinkUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddUrlTagLinkUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlTagRepository tagRepository;

    @InjectMocks
    private AddUrlTagLinkUseCaseImpl useCase;

    @Test
    void should_return_not_found_when_url_does_not_exist() {

        Long urlId = 1L;
        Long tagId = 2L;

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.empty());

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertTrue(result.isFailure());
        assertEquals(404, result.getStatusCode());
        assertEquals("Url not found", result.getMessage());

        verify(urlRepository).findById(urlId);

        verifyNoInteractions(tagRepository);

        verify(urlRepository, never())
                .save(any());
    }

    @Test
    void should_return_not_found_when_tag_does_not_exist() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        when(tagRepository.findById(tagId))
                .thenReturn(Optional.empty());

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertTrue(result.isFailure());
        assertEquals(404, result.getStatusCode());
        assertEquals("Tag not found", result.getMessage());

        verify(urlRepository).findById(urlId);
        verify(tagRepository).findById(tagId);

        verify(urlRepository, never())
                .save(any());
    }

    @Test
    void should_return_same_url_when_tag_already_exists() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);
        url.addTag("backend");

        UrlTagModel tag = new UrlTagModel();
        tag.setId(tagId);
        tag.setName("backend");

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getValue());
        assertSame(url, result.getValue());

        assertEquals(
                1,
                result.getValue().getTags().size()
        );

        verify(urlRepository)
                .findById(urlId);

        verify(tagRepository)
                .findById(tagId);

        verify(urlRepository, never())
                .save(any());
    }

    @Test
    void should_add_tag_and_save_url() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);

        UrlTagModel tag = new UrlTagModel();
        tag.setId(tagId);
        tag.setName("backend");

        UrlModel savedUrl = new UrlModel();
        savedUrl.setId(urlId);
        savedUrl.addTag("backend");

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));

        when(urlRepository.save(any(UrlModel.class)))
                .thenReturn(savedUrl);

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertTrue(result.isSuccess());
        assertEquals(201, result.getStatusCode());

        assertNotNull(result.getValue());

        assertTrue(
                result.getValue()
                        .getTags()
                        .contains("backend")
        );

        ArgumentCaptor<UrlModel> captor =
                ArgumentCaptor.forClass(
                        UrlModel.class
                );

        verify(urlRepository)
                .save(captor.capture());

        UrlModel captured =
                captor.getValue();

        assertTrue(
                captured.getTags()
                        .contains("backend")
        );

        verify(urlRepository)
                .findById(urlId);

        verify(tagRepository)
                .findById(tagId);
    }

    @Test
    void should_persist_exact_tag_name() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);

        UrlTagModel tag = new UrlTagModel();
        tag.setId(tagId);
        tag.setName("Java-Spring");

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));

        when(urlRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertTrue(result.isSuccess());

        assertTrue(
                result.getValue()
                        .getTags()
                        .contains("Java-Spring")
        );

        verify(urlRepository)
                .save(any());
    }
}