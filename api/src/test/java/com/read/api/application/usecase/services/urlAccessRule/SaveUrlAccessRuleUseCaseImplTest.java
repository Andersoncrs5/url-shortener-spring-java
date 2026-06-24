package com.read.api.application.usecase.services.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.SaveUrlAccessRuleUseCaseImpl;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaveUrlAccessRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private SaveUrlAccessRuleUseCaseImpl useCase;

    @Test
    void should_save_url_access_rule() {

        UrlAccessRuleModel rule = createUrlAccessRule();

        when(repository.save(rule))
                .thenReturn(rule);

        Result<UrlAccessRuleModel> result =
                useCase.execute(rule);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());

        assertEquals(
                200,
                result.getStatusCode()
        );

        assertEquals(
                rule.getId(),
                result.getValue().getId()
        );

        assertEquals(
                rule.getRuleValue(),
                result.getValue().getRuleValue()
        );

        assertEquals(
                rule.getType(),
                result.getValue().getType()
        );

        verify(repository).save(rule);
    }
}