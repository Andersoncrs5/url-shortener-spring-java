package com.write.api.application.service.urlAccessRuleService;

import com.write.api.application.service.urlAccessRule.DeleteUrlAccessRuleService;
import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUrlAccessRuleServiceTest {

    @Mock
    private IUrlAccessRuleRepository repository;

    @InjectMocks
    private DeleteUrlAccessRuleService service;

    @Test
    void shouldDeleteUrlAccessRuleSuccessfully() {
        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(1);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNotFoundWhenUrlAccessRuleDoesNotExist() {
        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(0);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Access Rule not found");

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldDeleteOnlyOneRecord() {
        Long id = 999L;

        when(repository.deleteById(id))
                .thenReturn(1);

        service.execute(id);

        verify(repository, times(1))
                .deleteById(id);
    }
}