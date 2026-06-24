package com.read.api.application.usecase.cdc;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.cdc.url.UrlCdcMapper;
import com.read.api.application.usecase.impl.cdc.url.UrlCdcServiceUseCaseImpl;
import com.read.api.application.usecase.interfaces.url.DeleteUrlByIdUseCase;
import com.read.api.application.usecase.interfaces.url.InsertUrlUseCase;
import com.read.api.application.usecase.interfaces.url.SaveUrlUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.model.UrlModel;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UrlCdcServiceUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private InsertUrlUseCase insert;

    @Mock
    private SaveUrlUseCase save;

    @Mock
    private DeleteUrlByIdUseCase delete;

    @Mock
    private UrlCdcMapper mapper;

    @InjectMocks
    private UrlCdcServiceUseCaseImpl useCase;

    @Test
    void should_ignore_duplicate_event() {

        TiCdcEvent<UrlCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("urls");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(redis.exists("urls:10:20"))
                .thenReturn(true);

        useCase.process(event);

        verify(redis)
                .exists("urls:10:20");

        verifyNoInteractions(
                insert,
                save,
                delete,
                mapper
        );
    }

    @Test
    void should_insert_url() {

        UrlModel url = createUrl();

        UrlCdcEvent cdc = mock(UrlCdcEvent.class);

        TiCdcEvent<UrlCdcEvent> event = mock(TiCdcEvent.class);

        when(event.table()).thenReturn("urls");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(url);

        when(insert.execute(url))
                .thenReturn(
                        Result.success(
                                url,
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
                .exists("urls:10:20");

        order.verify(mapper)
                .toModel(cdc);

        order.verify(insert)
                .execute(url);

        order.verify(redis)
                .save(
                        "urls:10:20",
                        "processed"
                );
    }

    @Test
    void should_update_url() {

        UrlModel url = createUrl();

        UrlCdcEvent cdc =
                mock(UrlCdcEvent.class);

        TiCdcEvent<UrlCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("urls");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isUpdate())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(url);

        when(save.execute(url))
                .thenReturn(
                        Result.success(
                                url,
                                200
                        )
                );

        useCase.process(event);

        verify(save)
                .execute(url);

        verify(redis)
                .save(
                        "urls:10:20",
                        "processed"
                );
    }

    @Test
    void should_delete_url() {

        UrlCdcEvent old =
                mock(UrlCdcEvent.class);

        when(old.id())
                .thenReturn(999L);

        TiCdcEvent<UrlCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("urls");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isDelete())
                .thenReturn(true);

        when(event.old())
                .thenReturn(List.of(old));

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(delete.execute(999L))
                .thenReturn(
                        Result.success()
                );

        useCase.process(event);

        ArgumentCaptor<Long> captor =
                ArgumentCaptor.forClass(Long.class);

        verify(delete)
                .execute(captor.capture());

        assertEquals(999L, captor.getValue());

        verify(redis)
                .save(
                        "urls:10:20",
                        "processed"
                );
    }

    @Test
    void should_not_save_event_when_insert_fails() {

        UrlModel url = createUrl();

        UrlCdcEvent cdc =
                mock(UrlCdcEvent.class);

        TiCdcEvent<UrlCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("urls");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(url);

        when(insert.execute(url))
                .thenReturn(
                        Result.failure(
                                "error",
                                500
                        )
                );

        useCase.process(event);

        verify(insert)
                .execute(url);

        verify(redis, never())
                .save(
                        anyString(),
                        anyString()
                );
    }

    @Test
    void should_not_delete_when_old_data_is_empty() {

        TiCdcEvent<UrlCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("urls");
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