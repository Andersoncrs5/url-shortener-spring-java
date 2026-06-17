package com.read.api.application.usecase.urlRedirectRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.InsertUrlRedirectRuleUseCaseImpl;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InsertUrlRedirectRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @InjectMocks
    private InsertUrlRedirectRuleUseCaseImpl useCase;

    @Test
    void should_insert_url_redirect_rule() {

        UrlRedirectRuleModel rule =
                new UrlRedirectRuleModel();

        rule.setId(1L);
        rule.setUrlId(100L);
        rule.setCountryCode("BR");
        rule.setRedirectUrl("https://google.com");
        rule.setActive(true);

        when(repository.insert(rule))
                .thenReturn(rule);

        Result<UrlRedirectRuleModel> result =
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
                rule.getRedirectUrl(),
                result.getValue().getRedirectUrl()
        );

        verify(repository)
                .insert(rule);
    }

    @Test
    void should_return_inserted_entity() {

        UrlRedirectRuleModel rule =
                new UrlRedirectRuleModel();

        rule.setId(99L);

        when(repository.insert(rule))
                .thenReturn(rule);

        Result<UrlRedirectRuleModel> result =
                useCase.execute(rule);

        assertSame(
                rule,
                result.getValue()
        );

        verify(repository)
                .insert(rule);
    }
}