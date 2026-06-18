package com.read.api.application.usecase.urlTag;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.ExistsUrlTagBySlugUseCaseImpl;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExistsUrlTagBySlugUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private ExistsUrlTagBySlugUseCaseImpl useCase;

    @Test
    void should_return_true_when_slug_exists() {

        String slug = "backend";

        when(repository.existsBySlug(slug))
                .thenReturn(true);

        Result<Boolean> result =
                useCase.execute(slug);

        assertThat(result).isNotNull();

        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.getValue())
                .isTrue();

        verify(repository)
                .existsBySlug(slug);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_false_when_slug_does_not_exist() {

        String slug = "non-existent-slug";

        when(repository.existsBySlug(slug))
                .thenReturn(false);

        Result<Boolean> result =
                useCase.execute(slug);

        assertThat(result).isNotNull();

        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.getValue())
                .isFalse();

        verify(repository)
                .existsBySlug(slug);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_delegate_slug_to_repository() {

        String slug = "java-spring";

        when(repository.existsBySlug(anyString()))
                .thenReturn(true);

        useCase.execute(slug);

        verify(repository)
                .existsBySlug(slug);
    }

    @Test
    void should_return_success_result() {

        String slug = "test";

        when(repository.existsBySlug(slug))
                .thenReturn(true);

        Result<Boolean> result =
                useCase.execute(slug);

        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(200);
    }
}