package com.write.api.application.service.urlTagLink;

import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUrlTagLinkByIdServiceTest {

    @Mock
    private IUrlTagLinkRepository repository;

    @InjectMocks
    private DeleteUrlTagLinkByIdService service;

    @Test
    void shouldDeleteUrlTagLinkSuccessfully() {
        Long id = 1L;

        when(repository.deleteById(id))
                .thenReturn(1);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNull();

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenUrlTagLinkNotFound() {
        Long id = 999L;

        when(repository.deleteById(id))
                .thenReturn(0);

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Tag link not found");

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldPropagateUnexpectedException() {
        Long id = 1L;

        when(repository.deleteById(id))
                .thenThrow(new RuntimeException("database error"));

        try {
            service.execute(id);
        } catch (RuntimeException ex) {

            assertThat(ex.getMessage())
                    .isEqualTo("database error");
        }

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }
}