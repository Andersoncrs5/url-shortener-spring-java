package com.write.api.application.service.apiKey;

import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.mapper.apiKey.CreateApiKeyMapper;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateApiKeyServiceTest extends BaseServiceTest {

    @Mock
    private IUserRoleRepository userRoleRepository;

    @Mock
    private IApiKeyRepository repository;

    @Mock
    private CreateApiKeyMapper mapper;

    @Mock
    private CreateOutboxEventUseCase outbox;

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

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(mapper.toDomain(dto))
                .thenReturn(mappedModel);

        when(idGen.nextId())
                .thenReturn(generatedId);

        when(repository.insert(any(ApiKeyModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success());


        Result<String> result =
                service.execute(dto, userId);


        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(201);

        assertThat(result.getValue())
                .isNotBlank();


        ArgumentCaptor<ApiKeyModel> captor =
                ArgumentCaptor.forClass(ApiKeyModel.class);


        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verify(mapper)
                .toDomain(dto);

        verify(idGen)
                .nextId();

        verify(repository)
                .insert(captor.capture());


        ApiKeyModel captured =
                captor.getValue();


        assertThat(captured.getId())
                .isEqualTo(generatedId);

        assertThat(captured.getUserId())
                .isEqualTo(userId);

        assertThat(captured.getOwnerUserId())
                .isEqualTo(ownerUserId);

        assertThat(captured.getKeyHash())
                .isEqualTo(
                        service.sha256(result.getValue())
                );


        verify(outbox)
                .execute(any(CreateOutboxEventCommand.class));


        verifyNoMoreInteractions(
                userRoleRepository,
                mapper,
                idGen,
                repository,
                outbox
        );
    }


    @Test
    void shouldReturnConflictWhenApiKeyNameAlreadyExists() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(mapper.toDomain(dto))
                .thenReturn(mappedModel);

        when(idGen.nextId())
                .thenReturn(generatedId);


        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate",
                                new RuntimeException(
                                        "uk_api_keys_name"
                                )
                        )
                );


        Result<String> result =
                service.execute(dto, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(409);

        assertThat(result.getMessage())
                .isEqualTo(
                        "An API key with this name already exists"
                );


        verify(repository)
                .insert(any(ApiKeyModel.class));

        verifyNoInteractions(outbox);
    }


    @Test
    void shouldReturnConflictWhenGeneratedHashAlreadyExists() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(mapper.toDomain(dto))
                .thenReturn(mappedModel);

        when(idGen.nextId())
                .thenReturn(generatedId);


        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate",
                                new RuntimeException(
                                        "uk_api_keys_key_hash"
                                )
                        )
                );


        Result<String> result =
                service.execute(dto, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(409);

        assertThat(result.getMessage())
                .isEqualTo(
                        "Generated API key already exists"
                );


        verifyNoInteractions(outbox);
    }


    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("USER"));


        Result<String> result =
                service.execute(dto, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(403);

        assertThat(result.getMessage())
                .isEqualTo(
                        "Only ADMIN or SUPER_ADMIN can perform this action"
                );


        verify(userRoleRepository)
                .findRoleByUserId(userId);


        verifyNoInteractions(
                mapper,
                idGen,
                repository,
                outbox
        );
    }


    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(mapper.toDomain(dto))
                .thenReturn(mappedModel);

        when(idGen.nextId())
                .thenReturn(generatedId);


        when(repository.insert(any(ApiKeyModel.class)))
                .thenThrow(
                        new RuntimeException("unexpected")
                );


        assertThatThrownBy(
                () -> service.execute(dto, userId)
        )
                .isInstanceOf(
                        InternalServerErrorException.class
                )
                .hasMessage(
                        "Error creating api key"
                );


        verify(repository)
                .insert(any(ApiKeyModel.class));

        verifyNoInteractions(outbox);
    }


    @Test
    void shouldGenerateConsistentSha256Hash() {

        String hash1 =
                service.sha256("abc");

        String hash2 =
                service.sha256("abc");

        String hash3 =
                service.sha256("xyz");


        assertThat(hash1)
                .isEqualTo(hash2);

        assertThat(hash1)
                .isNotEqualTo(hash3);

        assertThat(hash1)
                .hasSize(64);
    }
}