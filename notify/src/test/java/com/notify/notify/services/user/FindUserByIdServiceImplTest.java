package com.notify.notify.services.user;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.provider.FindUserByIdServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("FindUserByIdServiceImpl Unit Tests")
class FindUserByIdServiceImplTest extends BaseServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private FindUserByIdServiceImpl service;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setActive(true);
        user.setEmailVerified(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldFindUserByIdSuccessfully() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        Result<UserEntity> result = service.execute(1L);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getErrors()).isEmpty();

        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(1L);
        assertThat(result.getValue().getName()).isEqualTo("John");
        assertThat(result.getValue().getEmail()).isEqualTo("john@test.com");

        InOrder order = inOrder(repository);
        order.verify(repository).findById(1L);

        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        Result<UserEntity> result = service.execute(1L);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getMessage().isPresent()).isTrue();
        assertThat(result.getMessage().get()).isEqualTo("User not found");
        assertThat(result.getErrors()).containsExactly("User not found");
        assertThat(result.getValue()).isNull();

        InOrder order = inOrder(repository);
        order.verify(repository).findById(1L);

        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCallRepositoryOnlyOnce() {

        when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        service.execute(1L);

        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
    }
}