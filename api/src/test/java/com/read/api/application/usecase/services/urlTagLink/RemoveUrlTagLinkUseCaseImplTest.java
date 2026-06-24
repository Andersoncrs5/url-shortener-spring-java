package com.read.api.application.usecase.services.urlTagLink;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTagLink.RemoveUrlTagLinkUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoveUrlTagLinkUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlTagRepository tagRepository;

    @InjectMocks
    private RemoveUrlTagLinkUseCaseImpl useCase;

    @Test
    void should_return_not_found_when_url_does_not_exist() {

        Long urlId = 1L;
        Long tagId = 2L;

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.empty());

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Url not found", result.getMessage());

        verify(urlRepository).findById(urlId);

        verifyNoInteractions(tagRepository);
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

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Tag not found", result.getMessage());

        verify(urlRepository).findById(urlId);
        verify(tagRepository).findById(tagId);

        verify(urlRepository, never())
                .save(any());
    }

    @Test
    void should_return_same_url_when_tag_is_not_linked() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);

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

        verify(urlRepository).findById(urlId);
        verify(tagRepository).findById(tagId);

        verify(urlRepository, never())
                .save(any());
    }

    @Test
    void should_remove_tag_and_save_url() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);

        HashSet<String> tags = new HashSet<>();
        tags.add("backend");

        url.setTags(tags);

        UrlTagModel tag = new UrlTagModel();
        tag.setId(tagId);
        tag.setName("backend");

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));

        when(urlRepository.save(url))
                .thenReturn(url);

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());

        assertNotNull(result.getValue());

        assertFalse(
                result.getValue()
                        .getTags()
                        .contains("backend")
        );

        verify(urlRepository).findById(urlId);
        verify(tagRepository).findById(tagId);
        verify(urlRepository).save(url);
    }

    @Test
    void should_return_saved_url_after_remove() {

        Long urlId = 1L;
        Long tagId = 2L;

        UrlModel url = new UrlModel();
        url.setId(urlId);

        url.addTag("java");

        UrlTagModel tag = new UrlTagModel();
        tag.setId(tagId);
        tag.setName("java");

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        when(tagRepository.findById(tagId))
                .thenReturn(Optional.of(tag));

        when(urlRepository.save(url))
                .thenReturn(url);

        Result<UrlModel> result =
                useCase.execute(urlId, tagId);

        assertSame(
                url,
                result.getValue()
        );

        verify(urlRepository)
                .save(url);
    }
}