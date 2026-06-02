package com.write.api.application.service.user;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserDeletedEvent;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    private IUserRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private DeleteUserByIdUserService service;

    private UserModel user;

    @BeforeEach
    void setup() {
        user = new UserModel();
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setActive(true);
    }

    @Test
    void shouldDeleteUser() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(200));
        when(repository.deleteById(user.getId())).thenReturn(1);

        Result<Void> result = service.deleteById(user.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNull();
        assertThat(result.getErrors()).isEmpty();

        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        InOrder order = inOrder(repository, outbox);
        order.verify(repository).findById(user.getId());
        order.verify(outbox).execute(captor.capture());
        order.verify(repository).deleteById(user.getId());

        CreateOutboxEventCommand command = captor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.USER);
        assertThat(command.aggregateId()).isEqualTo(user.getId());
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.USER_DELETED);
        assertThat(command.topic()).isEqualTo(TopicEnum.USER_DELETED);

        UserDeletedEvent payload = (UserDeletedEvent) command.payload();
        assertThat(payload.id()).isEqualTo(user.getId());
        assertThat(payload.name()).isEqualTo(user.getName());
        assertThat(payload.email()).isEqualTo(user.getEmail());

        verifyNoMoreInteractions(repository, outbox);
    }

    @Test
    void shouldFailBecauseUserNotFoundTheDeleteUser() {
        when(repository.findById(user.getId())).thenReturn(Optional.empty());

        Result<Void> result = service.deleteById(user.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors()).hasSize(1);
        assertThat(result.getErrors().getFirst()).isEqualTo("User not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(user.getId());
        verifyNoInteractions(outbox);
        verify(repository, never()).deleteById(anyLong());

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFailWhenOutboxFails() {
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.failure(500, "Failed to create outbox event"));

        Result<Void> result = service.deleteById(user.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getErrors().getFirst()).isEqualTo("Failed to create outbox event");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(user.getId());
        verify(outbox).execute(any(CreateOutboxEventCommand.class));
        verify(repository, never()).deleteById(anyLong());

        verifyNoMoreInteractions(repository, outbox);
    }
}