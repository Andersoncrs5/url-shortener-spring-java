package com.write.api.application.service.apiKey;

import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.application.mapper.apiKey.UpdateApiKeyMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateApiKeyServiceTest {

    @Mock
    private IApiKeyRepository repository;

    @Mock
    private IUserRoleRepository userRoleRepository;

    @Mock
    private UpdateApiKeyMapper mapper;

    @InjectMocks
    private UpdateApiKeyService service;

    private UpdateApiKeyDTO dto;
    private ApiKeyModel apiKey;

    private final Long apiKeyId = 100L;
    private final Long userId = 10L;

    @BeforeEach
    void setup() {

        dto = new UpdateApiKeyDTO(
                "New Name",
                false,
                LocalDateTime.now().plusDays(10)
        );

        apiKey = new ApiKeyModel();
        apiKey.setId(apiKeyId);
        apiKey.setName("Old Name");
        apiKey.setActive(true);
    }

    @Test
    void shouldUpdateApiKeySuccessfully() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(repository.save(any(ApiKeyModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<ApiKeyModel> result =
                service.execute(dto, apiKeyId, userId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        ArgumentCaptor<ApiKeyModel> captor =
                ArgumentCaptor.forClass(ApiKeyModel.class);

        verify(mapper).update(dto, apiKey);
        verify(repository).save(captor.capture());

        ApiKeyModel captured = captor.getValue();

        assertThat(captured.getId()).isEqualTo(apiKeyId);
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("USER"));

        Result<ApiKeyModel> result =
                service.execute(dto, apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);

        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).update(any(), any());
    }

    @Test
    void shouldReturnBadRequestWhenExpirationDateIsInPast() {

        UpdateApiKeyDTO invalidDto =
                new UpdateApiKeyDTO(
                        "Name",
                        true,
                        LocalDateTime.now().minusDays(1)
                );

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        Result<ApiKeyModel> result =
                service.execute(invalidDto, apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("Expiration date must be in the future");

        verify(repository, never()).findById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnNotFoundWhenApiKeyDoesNotExist() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.empty());

        Result<ApiKeyModel> result =
                service.execute(dto, apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Api key not found");

        verify(repository).findById(apiKeyId);
        verify(repository, never()).save(any());
        verify(mapper, never()).update(any(), any());
    }

    @Test
    void shouldReturnConflictWhenNameAlreadyExists() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(repository.save(any()))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate",
                                new RuntimeException("uk_api_keys_name")
                        )
                );

        Result<ApiKeyModel> result =
                service.execute(dto, apiKeyId, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("An API key with this name already exists");

        verify(mapper).update(dto, apiKey);
        verify(repository).save(any());
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(repository.save(any()))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(
                () -> service.execute(dto, apiKeyId, userId)
        )
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error updating api key");

        verify(mapper).update(dto, apiKey);
        verify(repository).save(any());
    }

    @Test
    void shouldAllowSuperAdminToUpdateApiKey() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(repository.save(any()))
                .thenReturn(apiKey);

        Result<ApiKeyModel> result =
                service.execute(dto, apiKeyId, userId);

        assertThat(result.isSuccess()).isTrue();

        verify(repository).findById(apiKeyId);
        verify(repository).save(any());
    }
}
