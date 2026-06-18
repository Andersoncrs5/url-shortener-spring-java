package com.read.api.application.usecase.urlTag;

import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlTag.FindFilterUrlTagUseCaseImpl;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FindFilterUrlTagUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlTagRepository repository;

    @InjectMocks
    private FindFilterUrlTagUseCaseImpl useCase;

    @Test
    void should_find_url_tags_using_filter() {

        UrlTagFilter filter =
                new UrlTagFilter();

        filter.setUserId(10L);
        filter.setName("backend");
        filter.setActive(true);

        Pageable pageable =
                PageRequest.of(
                        0,
                        10,
                        Sort.by("name")
                );

        UrlTagModel tag =
                new UrlTagModel();

        tag.setId(1L);
        tag.setUserId(10L);
        tag.setName("backend");

        Page<UrlTagModel> expected =
                new PageImpl<>(
                        List.of(tag),
                        pageable,
                        1
                );

        when(repository.findAll(
                filter,
                pageable
        )).thenReturn(expected);

        Page<UrlTagModel> result =
                useCase.execute(
                        filter,
                        pageable
                );

        assertThat(result).isNotNull();

        assertThat(result.getTotalElements())
                .isEqualTo(1);

        assertThat(result.getContent())
                .hasSize(1);

        assertThat(result.getContent().getFirst().getId())
                .isEqualTo(1L);

        assertThat(result.getContent().getFirst().getName())
                .isEqualTo("backend");

        ArgumentCaptor<UrlTagFilter> filterCaptor =
                ArgumentCaptor.forClass(
                        UrlTagFilter.class
                );

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(
                        Pageable.class
                );

        verify(repository).findAll(
                filterCaptor.capture(),
                pageableCaptor.capture()
        );

        assertThat(filterCaptor.getValue().getUserId())
                .isEqualTo(10L);

        assertThat(filterCaptor.getValue().getName())
                .isEqualTo("backend");

        assertThat(filterCaptor.getValue().getActive())
                .isTrue();

        assertThat(pageableCaptor.getValue().getPageNumber())
                .isEqualTo(0);

        assertThat(pageableCaptor.getValue().getPageSize())
                .isEqualTo(10);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_empty_page_when_nothing_is_found() {

        UrlTagFilter filter =
                new UrlTagFilter();

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<UrlTagModel> emptyPage =
                Page.empty(pageable);

        when(repository.findAll(
                filter,
                pageable
        )).thenReturn(emptyPage);

        Page<UrlTagModel> result =
                useCase.execute(
                        filter,
                        pageable
                );

        assertThat(result).isNotNull();

        assertThat(result.getContent())
                .isEmpty();

        assertThat(result.getTotalElements())
                .isZero();

        verify(repository)
                .findAll(
                        filter,
                        pageable
                );

        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_call_repository_only_once() {

        UrlTagFilter filter =
                new UrlTagFilter();

        Pageable pageable =
                PageRequest.of(0, 20);

        when(repository.findAll(
                filter,
                pageable
        )).thenReturn(
                Page.empty(pageable)
        );

        useCase.execute(
                filter,
                pageable
        );

        verify(repository, times(1))
                .findAll(
                        filter,
                        pageable
                );

        verifyNoMoreInteractions(repository);
    }
}