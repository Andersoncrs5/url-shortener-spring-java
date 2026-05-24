package com.write.api.application.service.urlTag;

import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IUrlTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteByIdServiceTest {

    @Mock
    private IUrlTagRepository repository;

    @InjectMocks
    private DeleteByIdService service;

    @Test
    void shouldDeleteUrlTagSuccessfully() {
        when(repository.deleteById(1L))
                .thenReturn(1);

        Result<Void> result = service.execute(1L);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();

        verify(repository).deleteById(1L);
    }

    @Test
    void shouldReturn404WhenUrlTagNotFound() {
        when(repository.deleteById(999L))
                .thenReturn(0);

        Result<Void> result = service.execute(999L);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Tag not found");

        verify(repository).deleteById(999L);
    }
}