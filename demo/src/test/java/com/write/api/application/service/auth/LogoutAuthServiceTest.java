package com.write.api.application.service.auth;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutAuthServiceTest {

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private LogoutAuthService service;

    private UserModel user;

    private final Long userId = 1L;

    @BeforeEach
    void setup() {
        user = new UserModel();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setRefreshToken("old-refresh-token");
    }

    @Test
    void shouldLogoutSuccessfully() {
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        Result<UserModel> result = service.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getRefreshToken()).isNull();

        verify(repository).findById(userId);
        verify(repository).save(user);

        InOrder order = inOrder(repository);
        order.verify(repository).findById(userId);
        order.verify(repository).save(user);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(repository.findById(userId)).thenReturn(Optional.empty());

        Result<UserModel> result = service.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors().getFirst()).isEqualTo("User not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(userId);
        verify(repository, never()).save(any());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNullRefreshTokenBeforeSave() {
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(userId);

        assertThat(user.getRefreshToken()).isNull();

        verify(repository).save(user);
    }

    @Test
    void shouldCallRepositoryInOrder() {
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);

        service.execute(userId);

        InOrder order = inOrder(repository);
        order.verify(repository).findById(userId);
        order.verify(repository).save(user);
    }
}