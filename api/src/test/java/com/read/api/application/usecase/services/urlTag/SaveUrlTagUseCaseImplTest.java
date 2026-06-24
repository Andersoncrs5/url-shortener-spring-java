package com.read.api.application.usecase.services.urlTag;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.SaveUrlTagUseCaseImpl;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SaveUrlTagUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private SaveUrlTagUseCaseImpl useCase;

    @Test
    void should_save_url_tag() {

        UrlTagModel tag = new UrlTagModel();

        tag.setId(1L);
        tag.setUserId(100L);
        tag.setName("backend");

        when(repository.save(tag)).thenReturn(tag);

        Result<UrlTagModel> result = useCase.execute(tag);

        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getValue());
        assertEquals(tag.getId(), result.getValue().getId());
        assertEquals(tag.getUserId(), result.getValue().getUserId());
        assertEquals(tag.getName(), result.getValue().getName());

        verify(repository).save(tag);
    }

    @Test
    void should_return_saved_entity() {

        UrlTagModel tag =
                new UrlTagModel();

        tag.setId(99L);

        when(repository.save(tag))
                .thenReturn(tag);

        Result<UrlTagModel> result =
                useCase.execute(tag);

        assertSame(
                tag,
                result.getValue()
        );

        verify(repository)
                .save(tag);
    }
}