package com.notify.notify.services.user;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.provider.DeleteUserByIdServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("DeleteUserByIdServiceImpl Unit Tests")
class DeleteUserByIdServiceImplTest extends BaseServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    DeleteUserByIdServiceImpl service;

    @Test
    @DisplayName("Should delete user successfully when user exists")
    void shouldDeleteUserSuccessfully() {
        Long userId = 1L;
        when(repository.deleteAndCount(userId)).thenReturn(1);

        Result<Void> result = service.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();

        verify(repository, times(1)).deleteAndCount(userId);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should return failure Result when user to delete is not found")
    void shouldReturnFailureWhenUserNotFound() {
        Long userId = 999L;
        when(repository.deleteAndCount(userId)).thenReturn(0);

        Result<Void> result = service.execute(userId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getFirstError()).get().isEqualTo("User not found");
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(repository, times(1)).deleteAndCount(userId);
        verifyNoMoreInteractions(repository);
    }
}