package com.read.api.application.usecase.services.urlTag;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.ExistsUrlTagByNameUseCaseImpl;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExistsUrlTagByNameUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private ExistsUrlTagByNameUseCaseImpl useCase;

    @Test
    void should_return_true_when_tag_name_exists() {

        String name = "backend";

        when(repository.existsByName(name))
                .thenReturn(true);

        Result<Boolean> result =
                useCase.execute(name);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        assertThat(result.getValue()).isTrue();

        verify(repository, times(1))
                .existsByName(name);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_false_when_tag_name_does_not_exist() {

        String name = "not-found";

        when(repository.existsByName(name))
                .thenReturn(false);

        Result<Boolean> result =
                useCase.execute(name);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        assertThat(result.getValue()).isFalse();

        verify(repository, times(1))
                .existsByName(name);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_delegate_name_to_repository() {

        String name = "java";

        when(repository.existsByName(anyString()))
                .thenReturn(true);

        useCase.execute(name);

        verify(repository).existsByName(name);
        verifyNoMoreInteractions(repository);
    }
}