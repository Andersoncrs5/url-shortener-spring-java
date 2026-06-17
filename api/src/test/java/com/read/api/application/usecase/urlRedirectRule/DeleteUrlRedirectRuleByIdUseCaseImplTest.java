package com.read.api.application.usecase.urlRedirectRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.DeleteUrlRedirectRuleByIdUseCaseImpl;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteUrlRedirectRuleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @InjectMocks
    private DeleteUrlRedirectRuleByIdUseCaseImpl useCase;

    @Test
    void should_delete_url_redirect_rule() {

        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(1);

        Result<Void> result =
                useCase.execute(id);

        assertTrue(
                result.isSuccess()
        );

        assertEquals(
                200,
                result.getStatusCode()
        );

        assertNull(
                result.getValue()
        );

        verify(repository)
                .deleteById(id);
    }

    @Test
    void should_return_not_found_when_rule_does_not_exist() {

        Long id = 999L;

        when(repository.deleteById(id))
                .thenReturn(0);

        Result<Void> result =
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

        verify(repository)
                .deleteById(id);
    }
}