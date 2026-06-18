package com.read.api.application.usecase.urlTag;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.FindUrlTagByIdUseCaseImpl;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FindUrlTagByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private FindUrlTagByIdUseCaseImpl useCase;

    @Test
    void should_return_url_tag_when_found() {

        Long id = 1L;

        UrlTagModel tag = new UrlTagModel();
        tag.setId(id);
        tag.setUserId(100L);
        tag.setName("backend");

        when(repository.findById(id))
                .thenReturn(Optional.of(tag));

        Result<UrlTagModel> result =
                useCase.execute(id);

        assertThat(result).isNotNull();

        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.isFailure())
                .isFalse();

        assertThat(result.getStatusCode())
                .isEqualTo(200);

        assertThat(result.getMessage())
                .isNull();

        assertThat(result.getValue())
                .isNotNull();

        assertThat(result.getValue().getId())
                .isEqualTo(id);

        assertThat(result.getValue().getUserId())
                .isEqualTo(100L);

        assertThat(result.getValue().getName())
                .isEqualTo("backend");

        ArgumentCaptor<Long> captor =
                ArgumentCaptor.forClass(Long.class);

        verify(repository)
                .findById(captor.capture());

        assertThat(captor.getValue())
                .isEqualTo(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_not_found_when_url_tag_does_not_exist() {

        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Result<UrlTagModel> result =
                useCase.execute(id);

        assertThat(result).isNotNull();

        assertThat(result.isSuccess())
                .isFalse();

        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(404);

        assertThat(result.getMessage())
                .isEqualTo("Url Tag not found");

        assertThat(result.getValue())
                .isNull();

        ArgumentCaptor<Long> captor =
                ArgumentCaptor.forClass(Long.class);

        verify(repository)
                .findById(captor.capture());

        assertThat(captor.getValue())
                .isEqualTo(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_query_repository_only_once() {

        Long id = 10L;

        UrlTagModel tag = new UrlTagModel();
        tag.setId(id);

        when(repository.findById(id))
                .thenReturn(Optional.of(tag));

        useCase.execute(id);

        verify(repository, times(1))
                .findById(id);

        verifyNoMoreInteractions(repository);
    }
}