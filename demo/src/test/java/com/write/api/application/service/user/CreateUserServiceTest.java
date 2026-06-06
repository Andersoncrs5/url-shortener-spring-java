package com.write.api.application.service.user;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private IUserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserService service;

    private UserModel input;
    private UserModel saved;

    @BeforeEach
    void setup() {
        input = new UserModel();
        input.setName("john");
        input.setEmail("john@test.com");
        input.setPasswordHash("123456");
        input.setActive(true);

        saved = new UserModel();
        saved.setId(1L);
        saved.setName("john");
        saved.setEmail("john@test.com");
        saved.setPasswordHash("encoded-password");
    }

    @Test
    void shouldCreateUserSuccessfully() {

        when(passwordEncoder.encode("123456"))
                .thenReturn("encoded-password");

        when(repository.insert(any(UserModel.class)))
                .thenReturn(saved);

        Result<UserModel> result = service.create(input);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);

        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(1L);

        ArgumentCaptor<UserModel> captor =
                ArgumentCaptor.forClass(UserModel.class);

        verify(repository).insert(captor.capture());

        UserModel captured = captor.getValue();

        assertThat(captured.getPasswordHash())
                .isEqualTo("encoded-password");

        InOrder inOrder = inOrder(passwordEncoder, repository);

        inOrder.verify(passwordEncoder).encode("123456");
        inOrder.verify(repository).insert(any(UserModel.class));
    }

    @Test
    void shouldReturnEmailAlreadyExists() {

        DataIntegrityViolationException ex =
                mock(DataIntegrityViolationException.class, RETURNS_DEEP_STUBS);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(ex.getMostSpecificCause().getMessage())
                .thenReturn("duplicate key ruleValue violates uk_users_email");

        when(repository.insert(any()))
                .thenThrow(ex);

        UserModel user = new UserModel();
        user.setPasswordHash("123");

        Result<UserModel> result = service.create(user);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Email already exists");

        verify(passwordEncoder).encode("123");
        verify(repository).insert(any(UserModel.class));
    }

    @Test
    void shouldReturnUsernameAlreadyExists() {

        DataIntegrityViolationException ex =
                mock(DataIntegrityViolationException.class, RETURNS_DEEP_STUBS);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(ex.getMostSpecificCause().getMessage())
                .thenReturn("duplicate key uk_users_name");

        when(repository.insert(any()))
                .thenThrow(ex);

        UserModel user = new UserModel();
        user.setPasswordHash("123");

        Result<UserModel> result = service.create(user);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Username already exists");

        verify(passwordEncoder).encode("123");
        verify(repository).insert(any(UserModel.class));
    }

    @Test
    void shouldReturnGenericDatabaseError() {

        DataIntegrityViolationException ex =
                mock(DataIntegrityViolationException.class, RETURNS_DEEP_STUBS);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(ex.getMostSpecificCause().getMessage())
                .thenReturn("some random constraint error");

        when(repository.insert(any()))
                .thenThrow(ex);

        UserModel user = new UserModel();
        user.setPasswordHash("123");

        Result<UserModel> result = service.create(user);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("Database integrity error");

        verify(passwordEncoder).encode("123");
        verify(repository).insert(any(UserModel.class));
    }

    @Test
    void shouldThrowInternalServerErrorException() {

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(repository.insert(any()))
                .thenThrow(new RuntimeException("boom"));

        UserModel user = new UserModel();
        user.setPasswordHash("123");

        assertThatThrownBy(() -> service.create(user))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("boom");

        verify(passwordEncoder).encode("123");
        verify(repository).insert(any(UserModel.class));
    }
}