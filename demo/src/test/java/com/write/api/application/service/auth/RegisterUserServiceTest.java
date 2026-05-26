package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.mapper.auth.RegisterUserMapper;
import com.write.api.application.shared.Result;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.CreateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private CreateUserUseCase createUser;

    @Mock
    private RegisterUserMapper registerUserMapper;

    @Mock
    private TokenService tokenService;

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
                Collections.emptySet()
        );
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(Result.success(user, 201));
        when(tokenService.createTokens(user)).thenReturn(response);

        Result<AuthTokenResponseDTO> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().token()).isEqualTo("access-token");
        assertThat(result.getValue().refreshToken()).isEqualTo("refresh-token");

        InOrder order = inOrder(registerUserMapper, createUser, tokenService);
        order.verify(registerUserMapper).toDomain(dto);
        order.verify(createUser).create(user);
        order.verify(tokenService).createTokens(user);

        verifyNoMoreInteractions(registerUserMapper, createUser, tokenService);
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
        verifyNoInteractions(tokenService);

        verifyNoMoreInteractions(registerUserMapper, createUser);
    }

    @Test
    void shouldPassMappedUserToCreateUserUseCase() {
        when(registerUserMapper.toDomain(dto)).thenReturn(user);
        when(createUser.create(user)).thenReturn(Result.success(user, 201));
        when(tokenService.createTokens(user)).thenReturn(response);

        service.execute(dto);

        verify(registerUserMapper, times(1)).toDomain(dto);
        verify(createUser, times(1)).create(user);
        verify(tokenService, times(1)).createTokens(user);
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
}