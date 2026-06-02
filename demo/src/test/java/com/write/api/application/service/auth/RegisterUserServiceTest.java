package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserCreatedEvent;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.mapper.auth.RegisterUserMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.user.CreateUserUseCase;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock private CreateOutboxEventUseCase outbox;
    @Mock private IUserRepository repository;
    @Mock private CreateUserUseCase createUser;
    @Mock private RegisterUserMapper registerUserMapper;
    @Mock private TokenService tokenService;

    @InjectMocks
    private RegisterUserService service;

    private CreateUserDTO dto;
    private UserModel user;
    private AuthTokenResponseDTO response;

    @BeforeEach
    void setup() {
        dto = new CreateUserDTO(
                "John Doe",
                "john@test.com",
                "123456"
        );

        user = new UserModel();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@test.com");

        response = new AuthTokenResponseDTO(
                "access-token",
                "refresh-token",
                user,
                Collections.emptyList()
        );
    }

    @Test
    void shouldNotGenerateTokenWhenCreateFails() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(
                Result.failure(400, "Database integrity error")
        );

        Result<AuthTokenResponseDTO> result = service.execute(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);

        verify(registerUserMapper).toDomain(dto);
        verify(createUser).create(user);
        verify(tokenService, never()).createTokens(any());
    }

    @Test
    void shouldPassMappedUserToCreateUserUseCase() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(Result.success(user, 201));
        when(tokenService.createTokens(user)).thenReturn(response);

        when(repository.save(any(UserModel.class)))
                .thenReturn(user);

        when(outbox.execute(any()))
                .thenReturn(Result.success(null, 200));

        service.execute(dto);

        verify(registerUserMapper).toDomain(dto);
        verify(createUser).create(user);
        verify(tokenService).createTokens(user);
        verify(repository).save(user);
        verify(outbox).execute(any());
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(Result.success(user, 201));
        when(tokenService.createTokens(user)).thenReturn(response);
        when(repository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<AuthTokenResponseDTO> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().token()).isEqualTo("access-token");
        assertThat(result.getValue().refreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(UserModel.class);
        verify(repository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRefreshToken()).isEqualTo("refresh-token");

        ArgumentCaptor<CreateOutboxEventCommand> commandCaptor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);
        verify(outbox).execute(commandCaptor.capture());

        CreateOutboxEventCommand command = commandCaptor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.USER);
        assertThat(command.aggregateId()).isEqualTo(user.getId());
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.USER_CREATED);
        assertThat(command.topic()).isEqualTo(TopicEnum.USER_CREATED);
        assertThat(command.payload()).isInstanceOf(UserCreatedEvent.class);

        InOrder order = inOrder(registerUserMapper, createUser, tokenService, repository, outbox);
        order.verify(registerUserMapper).toDomain(dto);
        order.verify(createUser).create(user);
        order.verify(tokenService).createTokens(user);
        order.verify(repository).save(user);
        order.verify(outbox).execute(any(CreateOutboxEventCommand.class));

        verifyNoMoreInteractions(registerUserMapper, createUser, tokenService, repository, outbox);
    }

    @Test
    void shouldReturnFailureWhenCreateUserFails() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(
                Result.failure(409, "Email already exists")
        );

        Result<AuthTokenResponseDTO> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors().getFirst()).isEqualTo("Email already exists");
        assertThat(result.getValue()).isNull();

        verify(registerUserMapper).toDomain(dto);
        verify(createUser).create(user);
        verifyNoInteractions(tokenService, repository, outbox);

        verifyNoMoreInteractions(registerUserMapper, createUser);
    }

    @Test
    void shouldReturnFailureWhenOutboxFails() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(Result.success(user, 201));
        when(tokenService.createTokens(user)).thenReturn(response);
        when(repository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.failure(500, "Failed to create outbox event"));

        Result<AuthTokenResponseDTO> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getErrors().getFirst()).isEqualTo("Failed to create outbox event");
        assertThat(result.getValue()).isNull();

        verify(registerUserMapper).toDomain(dto);
        verify(createUser).create(user);
        verify(tokenService).createTokens(user);
        verify(repository).save(user);
        verify(outbox).execute(any(CreateOutboxEventCommand.class));

        verifyNoMoreInteractions(registerUserMapper, createUser, tokenService, repository, outbox);
    }
}