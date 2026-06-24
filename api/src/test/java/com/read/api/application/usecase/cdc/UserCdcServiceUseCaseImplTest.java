package com.read.api.application.usecase.cdc;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.cdc.user.UserCdcMapper;
import com.read.api.application.usecase.impl.cdc.user.UserCdcServiceUseCaseImpl;
import com.read.api.application.usecase.impl.user.DeleteByIdUserUseCaseImpl;
import com.read.api.application.usecase.impl.user.InsertUserUseCaseImpl;
import com.read.api.application.usecase.impl.user.SaveUserUseCaseImpl;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;
import com.read.api.domain.model.UserModel;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserCdcServiceUseCaseImplTest extends BaseUseCaseTest {

    @Mock private InsertUserUseCaseImpl insert;
    @Mock private SaveUserUseCaseImpl save;
    @Mock private DeleteByIdUserUseCaseImpl delete;
    @Mock private UserCdcMapper mapper;

    @InjectMocks private UserCdcServiceUseCaseImpl useCase;

    @Test
    void should_ignore_duplicate_event() {

        TiCdcEvent<UserCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("users");
        when(event.ts()).thenReturn(1L);
        when(event.es()).thenReturn(2L);

        when(redis.exists("users:1:2")).thenReturn(true);

        useCase.process(event);

        verify(redis).exists("users:1:2");

        verifyNoInteractions(
                insert,
                save,
                delete,
                mapper
        );
    }

    @Test
    void should_process_insert_and_save_idempotency_key() {

        UserCdcEvent cdc =
                new UserCdcEvent(
                        1L,
                        1L,
                        "Anderson",
                        "anderson@email.com",
                        null,
                        "hash",
                        null,
                        true,
                        true,
                        0,
                        null,
                        null,
                        null,
                        null
                );

        UserModel user = new UserModel();

        user.setEmail("anderson@email.com");

        TiCdcEvent<UserCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("users");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert()).thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists("users:10:20"))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(user);

        when(insert.execute(user))
                .thenReturn(Result.success(user));

        useCase.process(event);

        InOrder order =
                inOrder(
                        redis,
                        mapper,
                        insert
                );

        order.verify(redis)
                .exists("users:10:20");

        order.verify(mapper)
                .toModel(cdc);

        order.verify(insert)
                .execute(user);

        order.verify(redis)
                .save(
                        "users:10:20",
                        "processed"
                );
    }

    @Test
    void should_process_update() {

        UserCdcEvent cdc =
                mock(UserCdcEvent.class);

        UserModel user =
                new UserModel();

        TiCdcEvent<UserCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("users");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isUpdate())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(user);

        when(save.execute(user))
                .thenReturn(Result.success(user));

        useCase.process(event);

        verify(save)
                .execute(user);

        verify(redis)
                .save(
                        "users:10:20",
                        "processed"
                );
    }

    @Test
    void should_process_delete() {

        UserCdcEvent old =
                new UserCdcEvent(
                        99L,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        TiCdcEvent<UserCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("users");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isDelete()).thenReturn(true);
        when(event.old()).thenReturn(List.of(old));
        when(redis.exists(anyString())).thenReturn(false);
        when(delete.execute(99L)).thenReturn(Result.success());

        useCase.process(event);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(delete).execute(captor.capture());

        assertEquals(99L, captor.getValue());

        verify(redis)
                .save(
                        "users:10:20",
                        "processed"
                );
    }

    @Test
    void should_not_save_idempotency_key_when_insert_fails() {

        UserCdcEvent cdc =
                mock(UserCdcEvent.class);

        UserModel user =
                new UserModel();

        TiCdcEvent<UserCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("users");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(user);

        when(insert.execute(user))
                .thenReturn(
                        Result.failure(
                                "error",
                                500
                        )
                );

        useCase.process(event);

        verify(insert)
                .execute(user);

        verify(redis, never())
                .save(
                        anyString(),
                        anyString()
                );
    }
}
