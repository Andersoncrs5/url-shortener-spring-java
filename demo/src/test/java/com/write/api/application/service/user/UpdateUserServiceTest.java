package com.write.api.application.service.user;

import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.mapper.user.UserUpdateMapper;
import com.write.api.application.shared.Result;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserUpdateMapper mapper;

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private UpdateUserService service;

    private UserModel user;

    @BeforeEach
    void setup() {
        user = new UserModel();
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setPasswordHash("old-hash");
        user.setActive(true);
    }

    @Test
    void shouldUpdateUserWithoutPassword() {

        UpdateUserDTO dto = new UpdateUserDTO(
                "john-updated",
                "updated@test.com"
        );

        UserModel updatedUser = new UserModel();
        updatedUser.setId(1L);
        updatedUser.setName("john-updated");
        updatedUser.setEmail("updated@test.com");

        when(repository.save(user)).thenReturn(updatedUser);

        Result<UserModel> result = service.update(user, dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        assertThat(result.getValue().getName()).isEqualTo("john-updated");
        assertThat(result.getValue().getEmail()).isEqualTo("updated@test.com");

        verify(mapper, times(1))
                .updateUserFromDto(dto, user);

        verify(repository, times(1))
                .save(user);

        verify(passwordEncoder, never())
                .encode(anyString());

        verifyNoMoreInteractions(
                mapper,
                repository,
                passwordEncoder
        );
    }

    @Test
    void shouldUpdateUserWithPassword() {

        UpdateUserDTO dto = new UpdateUserDTO(
                "john-updated",
                "123456"
        );

        when(passwordEncoder.encode("123456"))
                .thenReturn("hashed-password");

        when(repository.save(user))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<UserModel> result = service.update(user, dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        assertThat(user.getPasswordHash())
                .isEqualTo("hashed-password");

        InOrder inOrder = inOrder(
                mapper,
                passwordEncoder,
                repository
        );

        inOrder.verify(mapper)
                .updateUserFromDto(dto, user);

        inOrder.verify(passwordEncoder)
                .encode("123456");

        inOrder.verify(repository)
                .save(user);

        verifyNoMoreInteractions(
                mapper,
                repository,
                passwordEncoder
        );
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {

        UpdateUserDTO dto = new UpdateUserDTO(
                "john",
                null
        );

        when(repository.save(user))
                .thenThrow(new RuntimeException("database error"));

        assertThatThrownBy(() -> service.update(user, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("database error");

        verify(mapper, times(1))
                .updateUserFromDto(dto, user);

        verify(repository, times(1))
                .save(user);

        verify(passwordEncoder, never())
                .encode(anyString());

        verifyNoMoreInteractions(
                mapper,
                repository,
                passwordEncoder
        );
    }

    @Test
    void shouldEncodePasswordOnlyOnce() {

        UpdateUserDTO dto = new UpdateUserDTO(
                null,
                "new-password"
        );

        when(passwordEncoder.encode("new-password"))
                .thenReturn("encoded-password");

        when(repository.save(any(UserModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.update(user, dto);

        ArgumentCaptor<UserModel> captor =
                ArgumentCaptor.forClass(UserModel.class);

        verify(repository).save(captor.capture());

        UserModel captured = captor.getValue();

        assertThat(captured.getPasswordHash())
                .isEqualTo("encoded-password");

        verify(passwordEncoder, times(1))
                .encode("new-password");
    }
}
