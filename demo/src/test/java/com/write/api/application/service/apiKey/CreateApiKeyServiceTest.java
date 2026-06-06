package com.write.api.application.service.apiKey;

import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.mapper.apiKey.CreateApiKeyMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateApiKeyServiceTest {

    @Mock
    private SnowflakeIdGenerator idGen;

    @Mock
    private IUserRoleRepository userRoleRepository;

    @Mock
    private IApiKeyRepository repository;

    @Mock
    private CreateApiKeyMapper mapper;

    @InjectMocks
    private CreateApiKeyService service;

    private CreateApiKeyDTO dto;
    private ApiKeyModel mappedModel;

    private final Long userId = 10L;
    private final Long ownerUserId = 111L;
    private final Long generatedId = 999L;

    @BeforeEach
    void setup() {
        dto = new CreateApiKeyDTO(
                "Production",
                LocalDateTime.now().plusDays(30),
                true,
                ownerUserId
        );

        mappedModel = new ApiKeyModel();
        mappedModel.setName(dto.name());
        mappedModel.setExpiresAt(dto.expiresAt());
        mappedModel.setActive(true);
        mappedModel.setOwnerUserId(ownerUserId);
    }

    @Test
    void shouldCreateApiKeySuccessfully() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.insert(any(ApiKeyModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<String> result = service.execute(dto, userId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotBlank();

        ArgumentCaptor<ApiKeyModel> captor =
                ArgumentCaptor.forClass(ApiKeyModel.class);

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(captor.capture());

        ApiKeyModel captured = captor.getValue();

        assertThat(captured.getId()).isEqualTo(generatedId);
        assertThat(captured.getUserId()).isEqualTo(userId);
        assertThat(captured.getName()).isEqualTo(dto.name());
        assertThat(captured.getExpiresAt()).isEqualTo(dto.expiresAt());
        assertThat(captured.getKeyHash()).isNotBlank();
        assertThat(captured.getKeyHash())
                .isEqualTo(service.sha256(result.getValue()));

        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldReturnConflictWhenApiKeyNameAlreadyExists() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_api_keys_name")
                ));

        Result<String> result = service.execute(dto, userId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("An API key with this name already exists");
        assertThat(result.getValue()).isNull();

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(ApiKeyModel.class));
        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldReturnConflictWhenGeneratedKeyHashAlreadyExists() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_api_keys_key_hash")
                ));

        Result<String> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Generated API key already exists");
        assertThat(result.getValue()).isNull();

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(ApiKeyModel.class));
        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_api_keys_user_id")
                ));

        Result<String> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User not found");
        assertThat(result.getValue()).isNull();

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(ApiKeyModel.class));
        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldReturnNotFoundWhenOwnerUserDoesNotExist() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("uk_api_keys_owner_name")
                ));

        Result<String> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Owner User not found");
        assertThat(result.getValue()).isNull();

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(ApiKeyModel.class));
        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldReturnDatabaseConstraintViolationWhenMostSpecificCauseIsNull() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<String> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Database constraint violation");

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(ApiKeyModel.class));
        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto, userId))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("Error creating api key");

        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(ApiKeyModel.class));
        verifyNoMoreInteractions(mapper, idGen, repository);
    }

    @Test
    void shouldGenerateConsistentSha256Hash() {
        String hash1 = service.sha256("abc");
        String hash2 = service.sha256("abc");
        String hash3 = service.sha256("xyz");

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(hash3);
        assertThat(hash1).hasSize(64);
    }

    @Test
    void shouldReturnForbiddenWhenUserIsNotAdminOrSuperAdmin() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("USER"));

        Result<String> result =
                service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getMessage())
                .isEqualTo("Only ADMIN or SUPER_ADMIN can perform this action");

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verifyNoInteractions(
                mapper,
                idGen,
                repository
        );
    }
}