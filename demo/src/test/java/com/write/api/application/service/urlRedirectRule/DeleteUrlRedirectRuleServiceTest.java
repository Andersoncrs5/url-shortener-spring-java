package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUrlRedirectRuleServiceTest {

    @Mock
    private IUrlRedirectRuleRepository repository;

    @InjectMocks
    private DeleteUrlRedirectRuleService service;

    private final Long id = 123L;

    @Test
    void shouldDeleteUrlRedirectRuleSuccessfully() {
        when(repository.deleteById(id)).thenReturn(1);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenUrlRedirectRuleDoesNotExist() {
        when(repository.deleteById(id)).thenReturn(0);

        Result<Void> result = service.execute(id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url Rule not found");

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404EvenWhenIdDoesNotExistTwice() {
        when(repository.deleteById(id)).thenReturn(0);

        Result<Void> result = service.execute(id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);

        verify(repository).deleteById(id);
    }

    @Test
    void shouldCallRepositoryExactlyOnce() {
        when(repository.deleteById(id)).thenReturn(1);

        service.execute(id);

        verify(repository, times(1)).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldHandleRepositoryFailureAsException() {
        when(repository.deleteById(id))
                .thenThrow(new RuntimeException("db error"));

        try {
            service.execute(id);
        } catch (Exception e) {
            assertThat(e.getMessage()).isEqualTo("db error");
        }

        verify(repository).deleteById(id);
    }
}