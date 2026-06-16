package com.read.api.application.usecase.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.DeleteUrlAccessRuleByIdUseCaseImpl;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeleteUrlAccessRuleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private DeleteUrlAccessRuleByIdUseCaseImpl useCase;

    @Test
    void should_delete_url_access_rule() {

        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(1);

        Result<Void> result = useCase.execute(id);

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals(200, result.getStatusCode());

        verify(repository).deleteById(id);
    }

    @Test
    void should_return_not_found_when_url_access_rule_does_not_exist() {

        Long id = 999L;

        when(repository.deleteById(id))
                .thenReturn(0);

        Result<Void> result = useCase.execute(id);

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());

        assertEquals(404, result.getStatusCode());
        assertEquals(
                "Url Access Rule not found",
                result.getMessage()
        );

        verify(repository).deleteById(id);
    }
}