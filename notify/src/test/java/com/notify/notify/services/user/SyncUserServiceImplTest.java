package com.notify.notify.services.user;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.provider.SyncUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncUserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private SyncUserServiceImpl service;

    private UserEntity entity;
    private UserEntity saved;

    @BeforeEach
    void setup() {

        entity = new UserEntity();
        entity.setId(1L);
        entity.setName("John");
        entity.setEmail("john@test.com");
        entity.setActive(true);
        entity.setEmailVerified(false);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        saved = new UserEntity();
        saved.setId(1L);
        saved.setName("John");
        saved.setEmail("john@test.com");
        saved.setActive(true);
        saved.setEmailVerified(false);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateUserSuccessfully() {

        when(repository.update(any(UserEntity.class)))
                .thenReturn(saved);

        Result<UserEntity> result = service.execute(entity);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getErrors()).isEmpty();

        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(1L);
        assertThat(result.getValue().getEmail()).isEqualTo("john@test.com");
        assertThat(result.getValue().getName()).isEqualTo("John");

        ArgumentCaptor<UserEntity> captor =
                ArgumentCaptor.forClass(UserEntity.class);

        InOrder order = inOrder(repository);

        order.verify(repository).update(captor.capture());

        UserEntity captured = captor.getValue();

        assertThat(captured.getId()).isEqualTo(entity.getId());
        assertThat(captured.getName()).isEqualTo(entity.getName());
        assertThat(captured.getEmail()).isEqualTo(entity.getEmail());

        verify(repository, times(1)).update(any(UserEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnForbiddenWhenEmailAlreadyExists() {

        DataIntegrityViolationException ex =
                mock(DataIntegrityViolationException.class, RETURNS_DEEP_STUBS);

        when(ex.getMostSpecificCause().getMessage())
                .thenReturn("duplicate key uk_users_email");

        when(repository.update(any(UserEntity.class)))
                .thenThrow(ex);

        Result<UserEntity> result = service.execute(entity);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getMessage().isPresent()).isTrue();
        assertThat(result.getMessage().get()).isEqualTo("Email already exists");
        assertThat(result.getValue()).isNull();

        verify(repository).update(any(UserEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnForbiddenWhenNameAlreadyExists() {

        DataIntegrityViolationException ex =
                mock(DataIntegrityViolationException.class, RETURNS_DEEP_STUBS);

        when(ex.getMostSpecificCause().getMessage())
                .thenReturn("duplicate key uk_users_name");

        when(repository.update(any(UserEntity.class)))
                .thenThrow(ex);

        Result<UserEntity> result = service.execute(entity);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getMessage().isPresent()).isTrue();
        assertThat(result.getMessage().get()).isEqualTo("Name already exists");
        assertThat(result.getValue()).isNull();

        verify(repository).update(any(UserEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldHandleDatabaseConstraintWhenMessageIsNull() {

        RuntimeException cause = mock(RuntimeException.class);

        when(cause.getMessage()).thenReturn(null);

        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("constraint", cause);

        when(repository.update(any(UserEntity.class)))
                .thenThrow(ex);

        Result<UserEntity> result = service.execute(entity);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isNotNull();

        verify(repository).update(any(UserEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUnexpectedExceptionOccurs() {

        when(repository.update(any(UserEntity.class)))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> service.execute(entity))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("boom");

        verify(repository).update(any(UserEntity.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCallRepositoryOnlyOnce() {

        when(repository.update(any(UserEntity.class)))
                .thenReturn(saved);

        service.execute(entity);

        verify(repository, times(1)).update(any(UserEntity.class));
        verifyNoMoreInteractions(repository);
    }

}