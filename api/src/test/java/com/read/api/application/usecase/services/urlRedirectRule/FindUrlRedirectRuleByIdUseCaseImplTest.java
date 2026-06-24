package com.read.api.application.usecase.services.urlRedirectRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.FindUrlRedirectRuleByIdUseCaseImpl;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindUrlRedirectRuleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @InjectMocks
    private FindUrlRedirectRuleByIdUseCaseImpl useCase;

    @Test
    void should_find_url_redirect_rule_by_id() {

        Long id = 1L;

        UrlRedirectRuleModel rule =
                new UrlRedirectRuleModel();

        rule.setId(id);
        rule.setUrlId(100L);

        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        Result<UrlRedirectRuleModel> result =
                useCase.execute(id);

        assertTrue(
                result.isSuccess()
        );

        assertEquals(
                200,
                result.getStatusCode()
        );

        assertNotNull(
                result.getValue()
        );

        assertEquals(
                id,
                result.getValue().getId()
        );

        verify(repository)
                .findById(id);
    }

    @Test
    void should_return_not_found_when_url_redirect_rule_does_not_exist() {

        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Result<UrlRedirectRuleModel> result =
                useCase.execute(id);

        assertTrue(
                result.isFailure()
        );

        assertEquals(
                404,
                result.getStatusCode()
        );

        assertEquals(
                "Url Redirect Rule not found",
                result.getMessage()
        );

        assertNull(
                result.getValue()
        );

        verify(repository)
                .findById(id);
    }
}