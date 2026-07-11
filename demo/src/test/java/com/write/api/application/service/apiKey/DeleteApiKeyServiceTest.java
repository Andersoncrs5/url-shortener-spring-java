package com.write.api.application.service.apiKey;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.apiKey.ApiKeyDeletedEvent;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteApiKeyServiceTest extends BaseServiceTest {

    @Mock
    private IApiKeyRepository repository;

    @Mock
    private IUserRoleRepository userRoleRepository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private DeleteApiKeyService service;


    private final Long apiKeyId = 100L;
    private final Long userId = 10L;


    private ApiKeyModel apiKey;


    @BeforeEach
    void setup() {

        apiKey = new ApiKeyModel();

        apiKey.setId(apiKeyId);
        apiKey.setName("Production");
        apiKey.setUserId(userId);
        apiKey.setOwnerUserId(20L);
        apiKey.setActive(true);
    }


    @Test
    void shouldDeleteApiKeySuccessfullyWhenUserIsAdmin() {

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(1);

        when(outbox.execute(any()))
                .thenReturn(Result.success());


        Result<Void> result =
                service.execute(apiKeyId, userId);


        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(200);


        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);


        verify(repository)
                .findById(apiKeyId);

        verify(userRoleRepository)
                .findRoleByUserId(userId);

        verify(repository)
                .deleteById(apiKeyId);

        verify(outbox)
                .execute(captor.capture());


        CreateOutboxEventCommand command =
                captor.getValue();


        assertThat(command.aggregateId())
                .isEqualTo(apiKeyId);


        ApiKeyDeletedEvent event =
                (ApiKeyDeletedEvent) command.payload();


        assertThat(event.id())
                .isEqualTo(apiKeyId);

        assertThat(event.name())
                .isEqualTo(apiKey.getName());

        assertThat(event.userId())
                .isEqualTo(apiKey.getUserId());


        verifyNoMoreInteractions(
                repository,
                userRoleRepository,
                outbox
        );
    }


    @Test
    void shouldDeleteApiKeySuccessfullyWhenUserIsSuperAdmin() {

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("SUPER_ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(1);

        when(outbox.execute(any()))
                .thenReturn(Result.success());


        Result<Void> result =
                service.execute(apiKeyId, userId);


        assertThat(result.isSuccess())
                .isTrue();


        verify(outbox)
                .execute(any());

        verify(repository)
                .deleteById(apiKeyId);
    }


    @Test
    void shouldReturnForbiddenWhenUserIsNotAdmin() {

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("USER"));


        Result<Void> result =
                service.execute(apiKeyId, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(403);

        assertThat(result.getMessage())
                .isEqualTo(
                        "Only ADMIN or SUPER_ADMIN can perform this action"
                );


        verify(repository)
                .findById(apiKeyId);

        verify(userRoleRepository)
                .findRoleByUserId(userId);


        verify(repository, never())
                .deleteById(any());

        verifyNoInteractions(outbox);
    }


    @Test
    void shouldReturnNotFoundWhenApiKeyDoesNotExist() {

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.empty());


        Result<Void> result =
                service.execute(apiKeyId, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(404);

        assertThat(result.getMessage())
                .isEqualTo("Api key not found");


        verify(repository)
                .findById(apiKeyId);


        verifyNoInteractions(
                userRoleRepository,
                outbox
        );
    }


    @Test
    void shouldReturnNotFoundWhenDeleteReturnsZero() {

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(0);


        Result<Void> result =
                service.execute(apiKeyId, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(404);

        assertThat(result.getMessage())
                .isEqualTo("Api key not found");


        verify(repository)
                .deleteById(apiKeyId);

        verifyNoInteractions(outbox);
    }


    @Test
    void shouldReturnFailureWhenOutboxFails() {

        when(repository.findById(apiKeyId))
                .thenReturn(Optional.of(apiKey));

        when(userRoleRepository.findRoleByUserId(userId))
                .thenReturn(List.of("ADMIN"));

        when(repository.deleteById(apiKeyId))
                .thenReturn(1);


        when(outbox.execute(any()))
                .thenReturn(
                        Result.failure(
                                "Outbox error",
                                500
                        )
                );


        Result<Void> result =
                service.execute(apiKeyId, userId);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(500);

        assertThat(result.getMessage())
                .isEqualTo("Outbox error");


        verify(outbox)
                .execute(any());
    }
}