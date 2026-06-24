package com.read.api.application.usecase.cdc;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.cdc.role.RoleCdcMapper;
import com.read.api.application.usecase.impl.cdc.role.RoleServiceUseCaseImpl;
import com.read.api.application.usecase.impl.role.DeleteRoleByIdUseCaseImpl;
import com.read.api.application.usecase.impl.role.InsertRoleUseCaseImpl;
import com.read.api.application.usecase.impl.role.SaveRoleUseCaseImpl;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;
import com.read.api.domain.model.RoleModel;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RoleServiceUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private InsertRoleUseCaseImpl insert;

    @Mock
    private SaveRoleUseCaseImpl save;

    @Mock
    private DeleteRoleByIdUseCaseImpl delete;

    @Mock
    private RoleCdcMapper mapper;

    @InjectMocks
    private RoleServiceUseCaseImpl useCase;

    @Test
    void should_ignore_duplicate_event() {

        TiCdcEvent<RoleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("roles");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(redis.exists("roles:10:20"))
                .thenReturn(true);

        useCase.process(event);

        verify(redis)
                .exists("roles:10:20");

        verifyNoInteractions(
                insert,
                save,
                delete,
                mapper
        );
    }

    @Test
    void should_insert_role() {

        RoleModel role = new RoleModel();
        role.setId(generator.nextId());
        role.setName("ADMIN");

        RoleCdcEvent cdc =
                mock(RoleCdcEvent.class);

        TiCdcEvent<RoleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("roles");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert()).thenReturn(true);
        when(event.firstData()).thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(role);

        when(insert.execute(role))
                .thenReturn(
                        Result.success(
                                role,
                                201
                        )
                );

        useCase.process(event);

        InOrder order =
                inOrder(
                        redis,
                        mapper,
                        insert
                );

        order.verify(redis)
                .exists("roles:10:20");

        order.verify(mapper)
                .toModel(cdc);

        order.verify(insert)
                .execute(role);

        order.verify(redis)
                .save(
                        "roles:10:20",
                        "processed"
                );
    }

    @Test
    void should_update_role() {

        RoleModel role = new RoleModel();
        role.setId(generator.nextId());
        role.setName("ADMIN");

        RoleCdcEvent cdc =
                mock(RoleCdcEvent.class);

        TiCdcEvent<RoleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("roles");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isUpdate()).thenReturn(true);
        when(event.firstData()).thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(role);

        when(save.execute(role))
                .thenReturn(
                        Result.success(
                                role,
                                200
                        )
                );

        useCase.process(event);

        verify(save)
                .execute(role);

        verify(redis)
                .save(
                        "roles:10:20",
                        "processed"
                );
    }

    @Test
    void should_delete_role() {

        RoleCdcEvent old =
                mock(RoleCdcEvent.class);

        when(old.id())
                .thenReturn(999L);

        TiCdcEvent<RoleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("roles");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isDelete())
                .thenReturn(true);

        when(event.old())
                .thenReturn(List.of(old));

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(delete.execute(999L))
                .thenReturn(Result.success());

        useCase.process(event);

        ArgumentCaptor<Long> captor =
                ArgumentCaptor.forClass(Long.class);

        verify(delete)
                .execute(captor.capture());

        assertEquals(
                999L,
                captor.getValue()
        );

        verify(redis)
                .save(
                        "roles:10:20",
                        "processed"
                );
    }

    @Test
    void should_not_save_event_when_insert_fails() {

        RoleModel role = new RoleModel();
        role.setId(generator.nextId());

        RoleCdcEvent cdc =
                mock(RoleCdcEvent.class);

        TiCdcEvent<RoleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("roles");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert()).thenReturn(true);
        when(event.firstData()).thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(role);

        when(insert.execute(role))
                .thenReturn(
                        Result.failure(
                                "error",
                                500
                        )
                );

        useCase.process(event);

        verify(redis, never())
                .save(
                        anyString(),
                        anyString()
                );
    }

    @Test
    void should_not_delete_when_old_data_is_empty() {

        TiCdcEvent<RoleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("roles");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isDelete())
                .thenReturn(true);

        when(event.old())
                .thenReturn(List.of());

        when(redis.exists(anyString()))
                .thenReturn(false);

        useCase.process(event);

        verify(delete, never())
                .execute(anyLong());

        verify(redis, never())
                .save(anyString(), anyString());
    }
}
