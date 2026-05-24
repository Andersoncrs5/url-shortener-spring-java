package com.write.api.application.service.url;

import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUrlByIdServiceTest {

    @Mock
    private IUrlRepository repository;

    @InjectMocks
    private DeleteUrlByIdService service;

    @Test
    void shouldDeleteUrlSuccessfully() {

        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(1);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(200);

        InOrder inOrder = inOrder(repository);

        inOrder.verify(repository, times(1))
                .deleteById(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenUrlDoesNotExist() {

        Long id = 999L;

        when(repository.deleteById(id))
                .thenReturn(0);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url not found");

        InOrder inOrder = inOrder(repository);

        inOrder.verify(repository, times(1))
                .deleteById(id);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCallDeleteOnlyOnce() {

        Long id = 10L;

        when(repository.deleteById(id))
                .thenReturn(1);

        service.execute(id);

        verify(repository, only())
                .deleteById(id);
    }

    @Test
    void shouldPropagateException() {

        Long id = 1L;

        RuntimeException exception =
                new RuntimeException("database error");

        when(repository.deleteById(id))
                .thenThrow(exception);

        try {
            service.execute(id);
        } catch (Exception ex) {

            assertThat(ex)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("database error");
        }

        verify(repository, times(1))
                .deleteById(id);

        verifyNoMoreInteractions(repository);
    }
}