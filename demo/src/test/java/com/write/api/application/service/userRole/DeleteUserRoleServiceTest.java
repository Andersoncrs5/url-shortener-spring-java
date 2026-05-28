package com.write.api.application.service.userRole;

import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserRoleServiceTest {

    @Mock
    private IUserRoleRepository repository;

    @InjectMocks
    private DeleteUserRoleService service;

    private final Long id = 1L;

    @Test
    void shouldDeleteUserRoleSuccessfully() {
        when(repository.deleteById(id)).thenReturn(1);

        Result<Void> result = service.deleteById(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNull();

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenUserRoleNotFound() {
        when(repository.deleteById(id)).thenReturn(0);

        Result<Void> result = service.deleteById(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User role not found");
        assertThat(result.getValue()).isNull();

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldPropagateUnexpectedException() {
        when(repository.deleteById(id))
                .thenThrow(new RuntimeException("db error"));

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("db error");

        verify(repository).deleteById(id);
        verifyNoMoreInteractions(repository);
    }
}