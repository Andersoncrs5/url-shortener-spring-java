package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserLoginBlockedEvent;
import com.write.api.application.dto.outbox.events.user.UserLoginFailEvent;
import com.write.api.application.dto.outbox.events.user.UserLoginSuccessEvent;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.UserModel;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserServiceTest {

    @Mock private IUserRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenService tokenService;
    @Mock private CreateOutboxEventUseCase outbox;

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
        user.setAttemptsLoginFailed(0);

        dto = new LoginUserDTO(user.getEmail(), "123456");

        response = new AuthTokenResponseDTO(
                "access-token",
                "refresh-token",
                user,
                List.of("USER")
        );
    }

    @Test
    void shouldFailBecauseUserNotFound() {
        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.empty());

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors()).containsExactly("Access denied");
        assertThat(result.getValue()).isNull();

        verify(repository).findByEmailIgnoreCase(user.getEmail());
        verifyNoInteractions(passwordEncoder, tokenService, outbox);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFailBecauseUserBlocked() {
        UserModel blocked = new UserModel();
        blocked.setId(2L);
        blocked.setName("blocked");
        blocked.setEmail("blocked@test.com");
        blocked.setBlockedAt(LocalDateTime.now().plusHours(1));

        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(blocked));

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(423);
        assertThat(result.getErrors()).containsExactly("User blocked");
        assertThat(result.getValue()).isNull();

        verify(repository).findByEmailIgnoreCase(user.getEmail());
        verifyNoInteractions(passwordEncoder, tokenService, outbox);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFailBecausePasswordIsWrongAndEmitLoginFailedEvent() {
        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(dto.password(), user.getPasswordHash()))
                .thenReturn(false);

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(401);
        assertThat(result.getErrors()).containsExactly("Access denied");
        assertThat(result.getValue()).isNull();
        assertThat(user.getAttemptsLoginFailed()).isEqualTo(1);
        assertThat(user.getBlockedAt()).isNull();

        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        InOrder order = inOrder(repository, passwordEncoder, outbox);
        order.verify(repository).findByEmailIgnoreCase(user.getEmail());
        order.verify(passwordEncoder).matches(dto.password(), user.getPasswordHash());
        order.verify(outbox).execute(captor.capture());
        order.verify(repository).save(user);

        CreateOutboxEventCommand command = captor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.USER);
        assertThat(command.aggregateId()).isEqualTo(user.getId());
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.USER_LOGIN_FAILED);
        assertThat(command.topic()).isEqualTo(TopicEnum.USER_LOGIN_FAILED);

        UserLoginFailEvent payload = (UserLoginFailEvent) command.payload();
        assertThat(payload.id()).isEqualTo(user.getId());
        assertThat(payload.name()).isEqualTo(user.getName());
        assertThat(payload.email()).isEqualTo(user.getEmail());
        assertThat(payload.attemptsLoginFailed()).isEqualTo(1);

        verifyNoInteractions(tokenService);
        verifyNoMoreInteractions(repository, passwordEncoder, outbox);
    }

    @Test
    void shouldBlockUserAfterThreeFailedAttemptsAndEmitBlockedEvent() {
        user.setAttemptsLoginFailed(2);

        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(dto.password(), user.getPasswordHash()))
                .thenReturn(false);

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(423);
        assertThat(result.getErrors()).containsExactly("User blocked");
        assertThat(result.getValue()).isNull();
        assertThat(user.getAttemptsLoginFailed()).isEqualTo(3);
        assertThat(user.getBlockedAt()).isNotNull();

        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        InOrder order = inOrder(repository, passwordEncoder, outbox);
        order.verify(repository).findByEmailIgnoreCase(user.getEmail());
        order.verify(passwordEncoder).matches(dto.password(), user.getPasswordHash());
        order.verify(outbox).execute(captor.capture());
        order.verify(repository).save(user);

        CreateOutboxEventCommand command = captor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.USER);
        assertThat(command.aggregateId()).isEqualTo(user.getId());
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.USER_BLOCKED);
        assertThat(command.topic()).isEqualTo(TopicEnum.USER_BLOCKED);

        UserLoginBlockedEvent payload = (UserLoginBlockedEvent) command.payload();
        assertThat(payload.id()).isEqualTo(user.getId());
        assertThat(payload.name()).isEqualTo(user.getName());
        assertThat(payload.email()).isEqualTo(user.getEmail());

        verifyNoInteractions(tokenService);
        verifyNoMoreInteractions(repository, passwordEncoder, outbox);
    }

    @Test
    void shouldLoginSuccessfullyAndEmitSuccessEvent() {
        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(dto.password(), user.getPasswordHash()))
                .thenReturn(true);

        when(tokenService.createTokens(user))
                .thenReturn(response);

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().token()).isEqualTo("access-token");
        assertThat(result.getValue().refreshToken()).isEqualTo("refresh-token");

        assertThat(user.getAttemptsLoginFailed()).isEqualTo(0);
        assertThat(user.getBlockedAt()).isNull();
        assertThat(user.getLastLoginAt()).isNotNull();
        assertThat(user.getRefreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        InOrder order = inOrder(repository, passwordEncoder, tokenService, outbox);
        order.verify(repository).findByEmailIgnoreCase(user.getEmail());
        order.verify(passwordEncoder).matches(dto.password(), user.getPasswordHash());
        order.verify(tokenService).createTokens(user);
        order.verify(repository).save(user);
        order.verify(outbox).execute(captor.capture());

        CreateOutboxEventCommand command = captor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.USER);
        assertThat(command.aggregateId()).isEqualTo(user.getId());
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.USER_LOGIN_SUCCESS);
        assertThat(command.topic()).isEqualTo(TopicEnum.USER_LOGIN_SUCCESS);

        UserLoginSuccessEvent payload = (UserLoginSuccessEvent) command.payload();
        assertThat(payload.id()).isEqualTo(user.getId());
        assertThat(payload.name()).isEqualTo(user.getName());
        assertThat(payload.email()).isEqualTo(user.getEmail());

        verifyNoMoreInteractions(repository, passwordEncoder, tokenService, outbox);
    }

    @Test
    void shouldFailWhenOutboxFailsDuringWrongPassword() {
        when(repository.findByEmailIgnoreCase(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(dto.password(), user.getPasswordHash()))
                .thenReturn(false);

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.failure(500, "Outbox error"));

        Result<AuthTokenResponseDTO> result = service.login(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getErrors()).containsExactly("Outbox error");
        assertThat(result.getValue()).isNull();

        verify(repository).findByEmailIgnoreCase(user.getEmail());
        verify(passwordEncoder).matches(dto.password(), user.getPasswordHash());
        verify(outbox).execute(any(CreateOutboxEventCommand.class));
        verify(repository, never()).save(any());
        verifyNoInteractions(tokenService);

        verifyNoMoreInteractions(repository, passwordEncoder, tokenService, outbox);
    }
}