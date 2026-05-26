package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginUserServiceTest {

    @Mock private IUserRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenService tokenService;

    @InjectMocks
    private LoginUserService service;

    private UserModel user;

    private LoginUserDTO dto;
    private AuthTokenResponseDTO response;

    @BeforeEach
    void setup() {
        user = new UserModel();
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setPasswordHash("encoded-password");

        dto = new LoginUserDTO(user.getEmail(), user.getPasswordHash());

        response = new AuthTokenResponseDTO(
                        "access-token",
                        "refresh-token",
                        user,
                        user.getRoles()
                );
    }

    @Test
    void shouldFailBecauseUserNotFound() {
        when(repository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.empty());

        Result<AuthTokenResponseDTO> result = this.service.login(dto);

        assertThat(result.getValue()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors().getFirst()).isEqualTo("Access denied");
        assertThat(result.isFailure()).isTrue();

        verify(repository, times(1)).findByEmailIgnoreCase(user.getEmail());

        verifyNoInteractions(passwordEncoder, tokenService);
    }

    @Test
    void shouldFailBecauseUserBlocked() {
        UserModel current = new UserModel();
        current.setId(2L);
        current.setBlockedAt(LocalDateTime.now().plusHours(1));

        when(repository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(current));

        Result<AuthTokenResponseDTO> result = this.service.login(dto);

        assertThat(result.getValue()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(423);
        assertThat(result.getErrors().getFirst()).isEqualTo("User blocked");
        assertThat(result.isFailure()).isTrue();

        verify(repository, times(1)).findByEmailIgnoreCase(user.getEmail());

        verifyNoInteractions(passwordEncoder, tokenService);
    }

    @Test
    void shouldFailBecausePasswordIsWrong() {
        when(repository.findByEmailIgnoreCase(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(dto.password()), eq(user.getPasswordHash()))).thenReturn(false);

        Result<AuthTokenResponseDTO> result = this.service.login(dto);

        assertThat(result.getValue()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors().getFirst()).isEqualTo("Access denied");
        assertThat(result.isFailure()).isTrue();

        verify(repository, times(1)).findByEmailIgnoreCase(user.getEmail());
        verify(passwordEncoder, times(1)).matches(dto.password(), user.getPasswordHash());

        verifyNoInteractions(tokenService);

        InOrder order = inOrder(repository, passwordEncoder);
        order.verify(repository).findByEmailIgnoreCase(user.getEmail());
        order.verify(passwordEncoder).matches(dto.password(), user.getPasswordHash());
    }

    @Test
    void shouldBlockUserAfterThreeFailedAttempts() {
        user.setAttemptsLoginFailed(2);

        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                eq(dto.password()),
                eq(user.getPasswordHash())
        )).thenReturn(false);

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(423);
        assertThat(result.getErrors().getFirst()).isEqualTo("User blocked");

        assertThat(user.getAttemptsLoginFailed()).isEqualTo(3);
        assertThat(user.getBlockedAt()).isNotNull();

        verify(repository).save(user);
        verify(tokenService, never()).createTokens(any());
    }

    @Test
    void shouldLoginSuccessfully() {


        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                eq(dto.password()),
                eq(user.getPasswordHash())
        )).thenReturn(true);

        when(tokenService.createTokens(user))
                .thenReturn(response);

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().token())
                .isEqualTo("access-token");

        assertThat(user.getAttemptsLoginFailed()).isEqualTo(0);
        assertThat(user.getBlockedAt()).isNull();
        assertThat(user.getLastLoginAt()).isNotNull();

        verify(repository).save(user);

        verify(tokenService, times(1))
                .createTokens(user);
    }

}
