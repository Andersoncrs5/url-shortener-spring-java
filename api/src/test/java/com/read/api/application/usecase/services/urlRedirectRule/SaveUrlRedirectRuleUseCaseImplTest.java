package com.read.api.application.usecase.services.urlRedirectRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.SaveUrlRedirectRuleUseCaseImpl;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SaveUrlRedirectRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @InjectMocks
    private SaveUrlRedirectRuleUseCaseImpl useCase;

    @Test
    void should_save_url_redirect_rule() {

        UrlRedirectRuleModel rule =
                new UrlRedirectRuleModel();

        rule.setId(1L);
        rule.setUrlId(100L);
        rule.setCountryCode("BR");
        rule.setRedirectUrl("https://google.com");
        rule.setActive(true);

        when(repository.save(rule))
                .thenReturn(rule);

        Result<UrlRedirectRuleModel> result =
                useCase.execute(rule);

        assertTrue(result.isSuccess());

        assertEquals(
                200,
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
                .save(rule);
    }

    @Test
    void should_return_saved_entity() {

        UrlRedirectRuleModel rule =
                new UrlRedirectRuleModel();

        rule.setId(99L);

        when(repository.save(rule))
                .thenReturn(rule);

        Result<UrlRedirectRuleModel> result =
                useCase.execute(rule);

        assertSame(
                rule,
                result.getValue()
        );

        verify(repository)
                .save(rule);
    }
}