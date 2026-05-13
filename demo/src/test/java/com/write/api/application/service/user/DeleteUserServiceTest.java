package com.write.api.application.service.user;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteUserServiceTest {

    @Mock
    private IUserRepository repository;

    @InjectMocks
    private DeleteUserByIdUserService service;

    UserModel user = new UserModel();

    @BeforeEach
    void setup() {
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setActive(true);
    }

    @Test
    void shouldDeleteUser() {
        when(repository.deleteById(user.getId())).thenReturn(1);

        Result<Void> result = this.service.deleteById(user.getId());

        assertThat(result.getValue()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getErrors().size()).isZero();

        verify(repository, times(1)).deleteById(user.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFailBecauseUserNotFoundTheDeleteUser() {
        when(repository.deleteById(user.getId())).thenReturn(0);

        Result<Void> result = this.service.deleteById(user.getId());

        assertThat(result.getValue()).isNull();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors().size()).isEqualTo(1);
        assertThat(result.getErrors().getFirst()).isEqualTo("User not found");

        verify(repository, times(1)).deleteById(user.getId());
        verifyNoMoreInteractions(repository);
    }



}
