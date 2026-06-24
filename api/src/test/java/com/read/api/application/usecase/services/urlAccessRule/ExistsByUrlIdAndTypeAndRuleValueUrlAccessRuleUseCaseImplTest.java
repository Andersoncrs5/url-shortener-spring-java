package com.read.api.application.usecase.services.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCaseImpl;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCaseImplTest
        extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCaseImpl useCase;

    @Test
    void shouldReturnTrueWhenRuleExists() {

        Long urlId = 1L;
        String ruleValue = "admin@gmail.com";
        UrlAccessRuleTypeEnum type =
                UrlAccessRuleTypeEnum.RATE_LIMIT;

        when(
                repository.existsUnique(
                        urlId,
                        type,
                        ruleValue
                )
        ).thenReturn(true);

        boolean result =
                useCase.execute(
                        urlId,
                        type,
                        ruleValue
                );

        assertTrue(result);

        verify(repository)
                .existsUnique(
                        urlId,
                        type,
                        ruleValue
                );
    }

    @Test
    void shouldReturnFalseWhenRuleDoesNotExist() {

        Long urlId = 1L;
        String ruleValue = "notfound@gmail.com";
        UrlAccessRuleTypeEnum type =
                UrlAccessRuleTypeEnum.RATE_LIMIT;

        when(
                repository.existsUnique(
                        urlId,
                        type,
                        ruleValue
                )
        ).thenReturn(false);

        boolean result =
                useCase.execute(
                        urlId,
                        type,
                        ruleValue
                );

        assertFalse(result);

        verify(repository)
                .existsUnique(
                        urlId,
                        type,
                        ruleValue
                );
    }
}