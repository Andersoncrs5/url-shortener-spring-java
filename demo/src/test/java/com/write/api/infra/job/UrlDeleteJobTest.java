package com.write.api.infra.job;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.infrastructure.scheduler.UrlDeleteJob;
import com.write.api.ports.in.url.DeleteUrlByIdForceUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlDeleteJobTest {

    @Mock
    private IUrlRepository repository;

    @Mock
    private DeleteUrlByIdForceUseCase deleteUrlByIdForce;

    @InjectMocks
    private UrlDeleteJob job;

    private UrlModel url1;
    private UrlModel url2;

    @BeforeEach
    void setup() {
        url1 = new UrlModel();
        url1.setId(1L);

        url2 = new UrlModel();
        url2.setId(2L);
    }

    @Test
    void shouldDeleteSingleUrlSuccessfully() {
        when(repository.findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        )).thenReturn(List.of(url1));

        when(deleteUrlByIdForce.execute(1L))
                .thenReturn(Result.success());

        job.delete();

        verify(repository).findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        );

        verify(deleteUrlByIdForce).execute(1L);
        verifyNoMoreInteractions(deleteUrlByIdForce);
    }

    @Test
    void shouldDeleteAllUrlsReturnedByRepository() {
        when(repository.findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        )).thenReturn(List.of(url1, url2));

        when(deleteUrlByIdForce.execute(anyLong()))
                .thenReturn(Result.success());

        job.delete();

        verify(deleteUrlByIdForce).execute(1L);
        verify(deleteUrlByIdForce).execute(2L);
        verify(deleteUrlByIdForce, times(2)).execute(anyLong());
    }

    @Test
    void shouldContinueProcessingWhenOneDeletionFails() {
        when(repository.findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        )).thenReturn(List.of(url1, url2));

        when(deleteUrlByIdForce.execute(1L))
                .thenReturn(Result.failure(500, "error"));

        when(deleteUrlByIdForce.execute(2L))
                .thenReturn(Result.success());

        job.delete();

        verify(deleteUrlByIdForce).execute(1L);
        verify(deleteUrlByIdForce).execute(2L);
    }

    @Test
    void shouldNotDeleteWhenRepositoryReturnsEmptyList() {
        when(repository.findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        job.delete();

        verify(repository).findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        );

        verifyNoInteractions(deleteUrlByIdForce);
    }

    @Test
    void shouldSearchUsingDeletedStatusAndLimit100() {
        when(repository.findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        job.delete();

        ArgumentCaptor<LocalDateTime> captor =
                ArgumentCaptor.forClass(LocalDateTime.class);

        verify(repository).findToDelete(
                eq(UrlStatusEnum.DELETED),
                eq(100),
                captor.capture()
        );

        LocalDateTime value = captor.getValue();

        assertThat(value)
                .isBeforeOrEqualTo(LocalDateTime.now().minusDays(7).plusSeconds(2))
                .isAfter(LocalDateTime.now().minusDays(7).minusMinutes(1));
    }
}