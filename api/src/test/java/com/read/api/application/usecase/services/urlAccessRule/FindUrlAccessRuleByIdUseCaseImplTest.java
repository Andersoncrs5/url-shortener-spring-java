package com.read.api.application.usecase.services.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.FindUrlAccessRuleByIdUseCaseImpl;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindUrlAccessRuleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private FindUrlAccessRuleByIdUseCaseImpl useCase;

    @Test
    void shouldReturnUrlAccessRuleWhenExists() {

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(rule));

        Result<UrlAccessRuleModel> result =
                useCase.execute(1L);

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());

        assertNotNull(result.getValue());
        assertEquals(1L, result.getValue().getId());
        assertEquals(200, result.getStatusCode());

        verify(repository).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenRuleDoesNotExist() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Result<UrlAccessRuleModel> result =
                useCase.execute(1L);

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());

        assertNull(result.getValue());
        assertEquals(404, result.getStatusCode());
        assertEquals(
                "Url Access Rule not found",
                result.getMessage()
        );

        verify(repository).findById(1L);
    }
}