package com.read.api.application.usecase.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.InsertUrlAccessRuleUseCaseImpl;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InsertUrlAccessRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private InsertUrlAccessRuleUseCaseImpl useCase;

    @Test
    void should_insert_url_access_rule() {

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setId(1L);
        rule.setUrlId(100L);
        rule.setRuleValue("admin@test.com");
        rule.setActive(true);

        when(repository.insert(rule))
                .thenReturn(rule);

        Result<UrlAccessRuleModel> result =
                useCase.execute(rule);

        assertTrue(result.isSuccess());

        assertEquals(
                201,
                result.getStatusCode()
        );

        assertNotNull(
                result.getValue()
        );

        assertEquals(
                rule.getId(),
                result.getValue().getId()
        );

        assertEquals(
                rule.getUrlId(),
                result.getValue().getUrlId()
        );

        assertEquals(
                rule.getRuleValue(),
                result.getValue().getRuleValue()
        );

        verify(repository)
                .insert(rule);
    }

    @Test
    void should_return_inserted_entity() {

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setId(99L);

        when(repository.insert(rule))
                .thenReturn(rule);

        Result<UrlAccessRuleModel> result =
                useCase.execute(rule);

        assertSame(
                rule,
                result.getValue()
        );

        verify(repository)
                .insert(rule);
    }
}