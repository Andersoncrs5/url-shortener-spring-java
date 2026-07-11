package com.write.api.application.service.user;

import com.write.api.application.dto.notification.WelcomeEmailEventDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.notification.WelcomeMessageNotificationUseCase;
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
    private WelcomeMessageNotificationUseCase welcomeMessage;

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

        when(welcomeMessage.execute(any(WelcomeEmailEventDTO.class)))
                .thenReturn(Result.success(new OutboxEventModel()));


        Result<UserModel> result = service.create(input);


        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);

        assertThat(result.getValue())
                .isNotNull();

        assertThat(result.getValue().getId())
                .isEqualTo(1L);


        ArgumentCaptor<WelcomeEmailEventDTO> dtoCaptor =
                ArgumentCaptor.forClass(WelcomeEmailEventDTO.class);


        verify(welcomeMessage)
                .execute(dtoCaptor.capture());


        WelcomeEmailEventDTO dto = dtoCaptor.getValue();


        assertThat(dto.userId())
                .isEqualTo(1L);

        assertThat(dto.email())
                .isEqualTo("john@test.com");

        assertThat(dto.name())
                .isEqualTo("john");


        InOrder inOrder =
                inOrder(passwordEncoder, repository, welcomeMessage);


        inOrder.verify(passwordEncoder)
                .encode("123456");


        inOrder.verify(repository)
                .insert(any(UserModel.class));


        inOrder.verify(welcomeMessage)
                .execute(any(WelcomeEmailEventDTO.class));
    }


    @Test
    void shouldFailWhenWelcomeNotificationCannotBeCreated() {

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");


        when(repository.insert(any(UserModel.class)))
                .thenReturn(saved);


        when(welcomeMessage.execute(any()))
                .thenReturn(
                        Result.failure(
                                "Could not create notification",
                                500
                        )
                );


        Result<UserModel> result =
                service.create(input);


        assertThat(result.isSuccess())
                .isFalse();


        assertThat(result.getStatusCode())
                .isEqualTo(500);


        assertThat(result.getMessage())
                .isEqualTo("Could not create notification");


        verify(repository)
                .insert(any(UserModel.class));


        verify(welcomeMessage)
                .execute(any(WelcomeEmailEventDTO.class));
    }


    @Test
    void shouldReturnEmailAlreadyExists() {

        DataIntegrityViolationException ex =
                mock(
                        DataIntegrityViolationException.class,
                        RETURNS_DEEP_STUBS
                );


        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");


        when(ex.getMostSpecificCause().getMessage())
                .thenReturn(
                        "duplicate key ruleValue violates uk_users_email"
                );


        when(repository.insert(any()))
                .thenThrow(ex);


        Result<UserModel> result =
                service.create(input);


        assertThat(result.isSuccess())
                .isFalse();


        assertThat(result.getStatusCode())
                .isEqualTo(409);


        assertThat(result.getMessage())
                .isEqualTo("Email already exists");


        verify(welcomeMessage, never())
                .execute(any());
    }


    @Test
    void shouldReturnUsernameAlreadyExists() {

        DataIntegrityViolationException ex =
                mock(
                        DataIntegrityViolationException.class,
                        RETURNS_DEEP_STUBS
                );


        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");


        when(ex.getMostSpecificCause().getMessage())
                .thenReturn(
                        "duplicate key uk_users_name"
                );


        when(repository.insert(any()))
                .thenThrow(ex);


        Result<UserModel> result =
                service.create(input);


        assertThat(result.isSuccess())
                .isFalse();


        assertThat(result.getStatusCode())
                .isEqualTo(409);


        assertThat(result.getMessage())
                .isEqualTo("Username already exists");


        verify(welcomeMessage, never())
                .execute(any());
    }


    @Test
    void shouldThrowInternalServerErrorException() {

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");


        when(repository.insert(any()))
                .thenThrow(
                        new RuntimeException("boom")
                );


        assertThatThrownBy(
                () -> service.create(input)
        )
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("boom");


        verify(welcomeMessage, never())
                .execute(any());
    }
}