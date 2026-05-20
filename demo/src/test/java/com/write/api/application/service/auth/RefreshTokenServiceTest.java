package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.shared.Result;
import com.write.api.config.security.jwt.TokenService;
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

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private IUserRepository repository;

    @Mock
    private TokenService service;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private String refreshToken;
    private UserModel user;
    private AuthTokenResponseDTO response;

    @BeforeEach
    void setup() {
        refreshToken = "old-refresh-token";

        user = new UserModel();
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setRefreshToken(refreshToken);
        user.setPasswordHash("encoded-password");

        response = new AuthTokenResponseDTO(
                "new-access-token",
                "new-refresh-token",
                user,
                Collections.emptySet()
        );
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        when(service.validateTokenV2(refreshToken))
                .thenReturn(Result.success("1"));

        when(repository.findByRefreshToken(refreshToken))
                .thenReturn(Optional.of(user));

        when(service.createTokens(user))
                .thenReturn(response);

        when(repository.save(any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<AuthTokenResponseDTO> result = refreshTokenService.execute(refreshToken);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().token()).isEqualTo("new-access-token");
        assertThat(result.getValue().refreshToken()).isEqualTo("new-refresh-token");

        ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(UserModel.class);
        verify(repository).save(userCaptor.capture());

        UserModel savedUser = userCaptor.getValue();
        assertThat(savedUser.getRefreshToken()).isEqualTo("new-refresh-token");

        InOrder order = inOrder(service, repository);
        order.verify(service).validateTokenV2(refreshToken);
        order.verify(repository).findByRefreshToken(refreshToken);
        order.verify(service).createTokens(user);
        order.verify(repository).save(user);

        verifyNoMoreInteractions(service, repository);
    }

    @Test
    void shouldFailWhenRefreshTokenIsInvalid() {
        when(service.validateTokenV2(refreshToken))
                .thenReturn(Result.failure(401, "Token inválido."));

        Result<AuthTokenResponseDTO> result = refreshTokenService.execute(refreshToken);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors().getFirst()).isEqualTo("Token inválido.");
        assertThat(result.getValue()).isNull();

        verify(service).validateTokenV2(refreshToken);
        verifyNoInteractions(repository);
        verify(service, never()).createTokens(any());

        verifyNoMoreInteractions(service);
    }

    @Test
    void shouldFailWhenUserNotFound() {
        when(service.validateTokenV2(refreshToken))
                .thenReturn(Result.success("1"));

        when(repository.findByRefreshToken(refreshToken))
                .thenReturn(Optional.empty());

        Result<AuthTokenResponseDTO> result = refreshTokenService.execute(refreshToken);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors().getFirst()).isEqualTo("User not found");
        assertThat(result.getValue()).isNull();

        InOrder order = inOrder(service, repository);
        order.verify(service).validateTokenV2(refreshToken);
        order.verify(repository).findByRefreshToken(refreshToken);

        verify(service, never()).createTokens(any());
        verify(repository, never()).save(any());

        verifyNoMoreInteractions(service, repository);
    }

    @Test
    void shouldFailWhenTokenSubjectDoesNotMatchUserId() {
        when(service.validateTokenV2(refreshToken))
                .thenReturn(Result.success("999"));

        when(repository.findByRefreshToken(refreshToken))
                .thenReturn(Optional.of(user));

        Result<AuthTokenResponseDTO> result = refreshTokenService.execute(refreshToken);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors().getFirst()).isEqualTo("Token mismatch");
        assertThat(result.getValue()).isNull();

        InOrder order = inOrder(service, repository);
        order.verify(service).validateTokenV2(refreshToken);
        order.verify(repository).findByRefreshToken(refreshToken);

        verify(service, never()).createTokens(any());
        verify(repository, never()).save(any());

        verifyNoMoreInteractions(service, repository);
    }

    @Test
    void shouldNotSaveWhenValidationFails() {
        when(service.validateTokenV2(refreshToken))
                .thenReturn(Result.failure(401, "Token expirado."));

        Result<AuthTokenResponseDTO> result = refreshTokenService.execute(refreshToken);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors().getFirst()).isEqualTo("Token expirado.");
        assertThat(result.getValue()).isNull();

        verify(service).validateTokenV2(refreshToken);
        verifyNoInteractions(repository);
        verify(service, never()).createTokens(any());

        verifyNoMoreInteractions(service);
    }
}