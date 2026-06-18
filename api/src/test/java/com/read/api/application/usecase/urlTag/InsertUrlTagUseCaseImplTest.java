package com.read.api.application.usecase.urlTag;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.InsertUrlTagUseCaseImpl;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InsertUrlTagUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private InsertUrlTagUseCaseImpl useCase;

    @Test
    void should_insert_url_tag() {

        UrlTagModel tag = new UrlTagModel();

        tag.setId(1L);
        tag.setUserId(100L);
        tag.setName("backend");

        when(repository.insert(tag))
                .thenReturn(tag);

        Result<UrlTagModel> result =
                useCase.execute(tag);

        assertThat(result).isNotNull();

        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.isFailure())
                .isFalse();

        assertThat(result.getStatusCode())
                .isEqualTo(200);

        assertThat(result.getValue())
                .isNotNull();

        assertThat(result.getValue().getId())
                .isEqualTo(1L);

        assertThat(result.getValue().getUserId())
                .isEqualTo(100L);

        assertThat(result.getValue().getName())
                .isEqualTo("backend");

        ArgumentCaptor<UrlTagModel> captor =
                ArgumentCaptor.forClass(
                        UrlTagModel.class
                );

        verify(repository)
                .insert(captor.capture());

        UrlTagModel captured =
                captor.getValue();

        assertThat(captured.getId())
                .isEqualTo(1L);

        assertThat(captured.getUserId())
                .isEqualTo(100L);

        assertThat(captured.getName())
                .isEqualTo("backend");

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_inserted_entity() {

        UrlTagModel tag = new UrlTagModel();

        tag.setId(99L);
        tag.setName("java");

        when(repository.insert(tag))
                .thenReturn(tag);

        Result<UrlTagModel> result =
                useCase.execute(tag);

        assertThat(result.getValue())
                .isSameAs(tag);

        verify(repository)
                .insert(tag);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_call_repository_only_once() {

        UrlTagModel tag = new UrlTagModel();

        tag.setId(10L);

        when(repository.insert(tag))
                .thenReturn(tag);

        useCase.execute(tag);

        verify(repository, times(1))
                .insert(tag);

        verifyNoMoreInteractions(repository);
    }
}