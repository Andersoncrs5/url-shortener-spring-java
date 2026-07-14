package com.write.api.application.service.user;

import com.write.api.application.dto.notification.ByeByeEmailEventDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserDeletedEvent;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.notification.ByeByeMessageNotificationUseCase;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class DeleteUserByIdUserServiceTest extends BaseServiceTest {


    @Mock
    private IUserRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @Mock
    private ByeByeMessageNotificationUseCase byeMessage;

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
    void shouldDeleteUserAndCreateNotificationsSuccessfully() {


        when(repository.findById(user.getId()))
                .thenReturn(Optional.of(user));


        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(new OutboxEventModel()));


        when(repository.deleteById(user.getId()))
                .thenReturn(1);


        when(byeMessage.execute(any(ByeByeEmailEventDTO.class)))
                .thenReturn(Result.success(new OutboxEventModel()));



        Result<Void> result =
                service.deleteById(user.getId());



        assertThat(result.isSuccess())
                .isTrue();


        assertThat(result.getStatusCode())
                .isEqualTo(200);



        ArgumentCaptor<CreateOutboxEventCommand> outboxCaptor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);



        InOrder order =
                inOrder(repository, outbox, byeMessage);



        order.verify(repository)
                .findById(user.getId());


        order.verify(outbox)
                .execute(outboxCaptor.capture());



        order.verify(repository)
                .deleteById(user.getId());



        order.verify(byeMessage)
                .execute(any(ByeByeEmailEventDTO.class));



        CreateOutboxEventCommand command =
                outboxCaptor.getValue();



        assertThat(command.aggregateType())
                .isEqualTo(AggregateTypeEnum.USER);



        assertThat(command.aggregateId())
                .isEqualTo(user.getId());



        assertThat(command.eventType())
                .isEqualTo(EventTypeEnum.USER_DELETED);



        assertThat(command.topic())
                .isEqualTo(TopicEnum.USER_DELETED);



        UserDeletedEvent payload =
                (UserDeletedEvent) command.payload();



        assertThat(payload.id())
                .isEqualTo(user.getId());



        assertThat(payload.name())
                .isEqualTo(user.getName());



        assertThat(payload.email())
                .isEqualTo(user.getEmail());



        verifyNoMoreInteractions(repository, outbox, byeMessage);

    }





    @Test
    void shouldReturn404WhenUserDoesNotExist() {


        when(repository.findById(user.getId()))
                .thenReturn(Optional.empty());



        Result<Void> result =
                service.deleteById(user.getId());



        assertThat(result.isFailure())
                .isTrue();



        assertThat(result.getStatusCode())
                .isEqualTo(404);



        assertThat(result.getMessage())
                .isEqualTo("User not found");



        verify(repository).findById(user.getId());
        verifyNoInteractions(outbox, byeMessage);

        verify(repository, never())
                .deleteById(anyLong());


    }

    @Test
    void shouldFailWhenCreatingDeleteOutboxEvent() {
        when(repository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(
                        Result.failure(
                                500,
                                "Failed to create outbox event"
                        )
                );

        Result<Void> result =
                service.deleteById(user.getId());

        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(500);

        assertThat(result.getMessage())
                .isEqualTo("Failed to create outbox event");

        verify(repository).findById(user.getId());
        verify(outbox).execute(any(CreateOutboxEventCommand.class));
        verify(repository, never()).deleteById(anyLong());

        verifyNoInteractions(byeMessage);
    }

    @Test
    void shouldThrowWhenDeleteReturnsWrongAmount() {


        when(repository.findById(user.getId()))
                .thenReturn(Optional.of(user));


        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(new OutboxEventModel()));



        when(repository.deleteById(user.getId()))
                .thenReturn(0);



        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> service.deleteById(user.getId())
                )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(
                        "Expected 1 row deleted"
                );



        verify(repository)
                .deleteById(user.getId());



        verifyNoInteractions(byeMessage);

    }


}