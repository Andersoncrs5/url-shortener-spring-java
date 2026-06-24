package com.read.api.application.usecase.services.urlAccessRule;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.FindAllFilterUrlAccessRuleUseCaseImpl;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class FindAllFilterUrlAccessRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private FindAllFilterUrlAccessRuleUseCaseImpl useCase;

    @Test
    void should_return_filtered_page() {

        UrlAccessRuleFilter filter = new UrlAccessRuleFilter();
        filter.setUrlId(10L);

        UrlAccessRuleModel rule1 = new UrlAccessRuleModel();
        rule1.setId(1L);
        rule1.setUrlId(10L);

        UrlAccessRuleModel rule2 = new UrlAccessRuleModel();
        rule2.setId(2L);
        rule2.setUrlId(10L);

        PageRequest pageable = PageRequest.of(0, 10);

        Page<UrlAccessRuleModel> expected = new PageImpl<>(
                List.of(rule1, rule2),
                pageable,
                2
        );

        when(repository.findAll(filter, pageable))
                .thenReturn(expected);

        Page<UrlAccessRuleModel> result =
                useCase.execute(filter, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(10L, result.getContent().getFirst().getUrlId());

        verify(repository).findAll(filter, pageable);
    }

    @Test
    void should_return_empty_page_when_no_rules_found() {

        UrlAccessRuleFilter filter = new UrlAccessRuleFilter();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<UrlAccessRuleModel> expected =
                Page.empty(pageable);

        when(repository.findAll(filter, pageable))
                .thenReturn(expected);

        Page<UrlAccessRuleModel> result =
                useCase.execute(filter, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());

        verify(repository).findAll(filter, pageable);
    }
}