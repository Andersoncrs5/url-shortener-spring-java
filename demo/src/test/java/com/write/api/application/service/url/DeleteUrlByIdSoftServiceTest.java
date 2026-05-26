package com.write.api.application.service.url;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUrlByIdSoftServiceTest {

    @Mock
    private IUrlRepository repository;

    @InjectMocks
    private DeleteUrlByIdSoftService service;

    private UrlModel url;
    private final Long id = 1L;

    @BeforeEach
    void setup() {
        url = new UrlModel();
        url.setId(id);
        url.setVersion(1L);
        url.setUserId(10L);
        url.setShortCode("abc123");
        url.setOriginalUrl("https://example.com");
        url.setTitle("Example");
        url.setDescription("Any desc");
        url.setStatus(UrlStatusEnum.ACTIVE);
        url.setDeletedAt(null);
        url.setCreatedAt(LocalDateTime.now().minusDays(1));
        url.setUpdatedAt(LocalDateTime.now().minusHours(1));
    }

    @Test
    void shouldSoftDeleteUrlSuccessfully() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(repository.save(any(UrlModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNull();

        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).save(captor.capture());

        UrlModel saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getStatus()).isEqualTo(UrlStatusEnum.DELETED);
        assertThat(saved.getDeletedAt()).isNotNull();

        InOrder order = inOrder(repository);
        order.verify(repository).findById(id);
        order.verify(repository).save(any(UrlModel.class));

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenUrlNotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(id);
        verify(repository, never()).save(any());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldMarkUrlAsDeletedBeforeSaving() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(repository.save(any(UrlModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(id);

        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).save(captor.capture());

        UrlModel saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(UrlStatusEnum.DELETED);
        assertThat(saved.getDeletedAt()).isNotNull();
    }
}