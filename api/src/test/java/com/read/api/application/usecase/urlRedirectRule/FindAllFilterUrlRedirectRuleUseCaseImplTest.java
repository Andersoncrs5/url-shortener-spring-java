package com.read.api.application.usecase.urlRedirectRule;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.FindAllFilterUrlRedirectRuleUseCaseImpl;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindAllFilterUrlRedirectRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @InjectMocks
    private FindAllFilterUrlRedirectRuleUseCaseImpl useCase;

    @Test
    void should_find_all_url_redirect_rules_by_filter() {

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setUrlId(100L);

        Pageable pageable =
                PageRequest.of(0, 10);

        UrlRedirectRuleModel rule =
                new UrlRedirectRuleModel();

        rule.setId(1L);
        rule.setUrlId(100L);

        Page<UrlRedirectRuleModel> expected =
                new PageImpl<>(List.of(rule));

        when(repository.findAll(filter, pageable))
                .thenReturn(expected);

        Page<UrlRedirectRuleModel> result =
                useCase.execute(filter, pageable);

        assertNotNull(result);

        assertEquals(
                1,
                result.getTotalElements()
        );

        assertEquals(
                rule.getId(),
                result.getContent().getFirst().getId()
        );

        verify(repository)
                .findAll(filter, pageable);
    }

    @Test
    void should_return_empty_page_when_no_results_found() {

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<UrlRedirectRuleModel> expected =
                Page.empty(pageable);

        when(repository.findAll(filter, pageable))
                .thenReturn(expected);

        Page<UrlRedirectRuleModel> result =
                useCase.execute(filter, pageable);

        assertNotNull(result);

        assertTrue(
                result.isEmpty()
        );

        assertEquals(
                0,
                result.getTotalElements()
        );

        verify(repository)
                .findAll(filter, pageable);
    }
}