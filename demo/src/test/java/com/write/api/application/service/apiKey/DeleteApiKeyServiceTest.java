package com.write.api.application.service.apiKey;

import com.write.api.application.shared.Result;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteApiKeyServiceTest {

    @Mock
    private IApiKeyRepository repository;

    @Mock
    private IUserRoleRepository userRoleRepository;

    @InjectMocks
    private DeleteApiKeyService service;

    private final Long apiKeyId = 100L;
    private final Long userId = 10L;

    @Test
    void shouldDeleteApiKeySuccessfullyWhenUserIsAdmin() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(1);

        Result<Void> result =
                service.execute(apiKeyId, userId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verify(repository)
                .deleteById(apiKeyId);

        verifyNoMoreInteractions(
                userRoleRepository,
                repository
        );
    }

    @Test
    void shouldDeleteApiKeySuccessfullyWhenUserIsSuperAdmin() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(1);

        Result<Void> result =
                service.execute(apiKeyId, userId);

        assertThat(result.isSuccess()).isTrue();

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verify(repository)
                .deleteById(apiKeyId);

        verifyNoMoreInteractions(
                userRoleRepository,
                repository
        );
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("USER"));

        Result<Void> result =
                service.execute(apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getMessage())
                .isEqualTo("Only ADMIN or SUPER_ADMIN can perform this action");

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verifyNoInteractions(repository);

        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    void shouldReturnForbiddenWhenUserHasNoRoles() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of());

        Result<Void> result =
                service.execute(apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verifyNoInteractions(repository);

        verifyNoMoreInteractions(userRoleRepository);
    }

    @Test
    void shouldReturnNotFoundWhenApiKeyDoesNotExist() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(0);

        Result<Void> result =
                service.execute(apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Api key not found");

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verify(repository)
                .deleteById(apiKeyId);

        verifyNoMoreInteractions(
                userRoleRepository,
                repository
        );
    }
}