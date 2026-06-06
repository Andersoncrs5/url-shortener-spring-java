package com.write.api.application.service.url;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUrlByIdForceServiceTest {

    @Mock
    private IUrlRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private DeleteUrlByIdForceService service;

    private UrlModel url;

    @BeforeEach
    void setup() {
        url = new UrlModel();
        url.setId(1L);
        url.setTitle("Google");
        url.setShortCode("abc123");
    }

    @Test
    void shouldDeleteUrlSuccessfully() {

        when(repository.findById(url.getId()))
                .thenReturn(Optional.of(url));

        when(outbox.execute(any()))
                .thenReturn(Result.success());

        Result<Void> result = service.execute(url.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        InOrder order = inOrder(repository, outbox);

        order.verify(repository).findById(url.getId());
        order.verify(outbox).execute(any());
        order.verify(repository).deleteById(url.getId());

        verifyNoMoreInteractions(repository, outbox);
    }

    @Test
    void shouldReturn404WhenUrlDoesNotExist() {

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        Result<Void> result = service.execute(999L);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors().getFirst())
                .isEqualTo("Url not found");

        verify(repository).findById(999L);

        verifyNoInteractions(outbox);

        verify(repository, never())
                .deleteById(any());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotDeleteWhenOutboxFails() {

        when(repository.findById(url.getId()))
                .thenReturn(Optional.of(url));

        when(outbox.execute(any()))
                .thenReturn(Result.failure(500, "Kafka unavailable"));

        Result<Void> result = service.execute(url.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);

        verify(repository).findById(url.getId());
        verify(outbox).execute(any());

        verify(repository, never())
                .deleteById(any());

        verifyNoMoreInteractions(repository, outbox);
    }

    @Test
    void shouldPropagateRepositoryException() {

        RuntimeException exception =
                new RuntimeException("database error");

        when(repository.findById(url.getId()))
                .thenThrow(exception);

        assertThatThrownBy(() -> service.execute(url.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("database error");

        verify(repository).findById(url.getId());

        verifyNoInteractions(outbox);
    }

    @Test
    void shouldCreateOutboxEventBeforeDelete() {

        when(repository.findById(url.getId()))
                .thenReturn(Optional.of(url));

        when(outbox.execute(any()))
                .thenReturn(Result.success());

        service.execute(url.getId());

        InOrder order = inOrder(repository, outbox);

        order.verify(repository).findById(url.getId());
        order.verify(outbox).execute(any());
        order.verify(repository).deleteById(url.getId());
    }
}