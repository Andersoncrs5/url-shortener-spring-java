package com.read.api.application.usecase.cdc;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.cdc.urlTag.UrlTagCdcMapper;
import com.read.api.application.usecase.impl.cdc.urlTag.UrlTagCdcServiceUseCaseImpl;
import com.read.api.application.usecase.impl.urlTag.DeleteUrlTagByIdUseCaseImpl;
import com.read.api.application.usecase.impl.urlTag.InsertUrlTagUseCaseImpl;
import com.read.api.application.usecase.impl.urlTag.SaveUrlTagUseCaseImpl;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlTagCdcEvent;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UrlTagCdcServiceUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private InsertUrlTagUseCaseImpl insert;

    @Mock
    private SaveUrlTagUseCaseImpl save;

    @Mock
    private DeleteUrlTagByIdUseCaseImpl delete;

    @Mock
    private UrlTagCdcMapper mapper;

    @InjectMocks
    private UrlTagCdcServiceUseCaseImpl useCase;

    @Test
    void shouldIgnoreDuplicateEvent() {
        TiCdcEvent<UrlTagCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_tags");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(redis.exists("url_tags:10:20"))
                .thenReturn(true);

        useCase.process(event);

        verify(redis)
                .exists("url_tags:10:20");

        verifyNoInteractions(
                insert,
                save,
                delete,
                mapper
        );
    }

    @Test
    void shouldInsertTag() {

        UrlTagModel tag = new UrlTagModel();
        tag.setId(generator.nextId());
        tag.setName("java");

        UrlTagCdcEvent cdc =
                mock(UrlTagCdcEvent.class);

        TiCdcEvent<UrlTagCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_tags");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert()).thenReturn(true);
        when(event.firstData()).thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(tag);

        when(insert.execute(tag))
                .thenReturn(
                        Result.success(
                                tag,
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
                .exists("url_tags:10:20");

        order.verify(mapper)
                .toModel(cdc);

        order.verify(insert)
                .execute(tag);

        order.verify(redis)
                .save(
                        "url_tags:10:20",
                        "processed"
                );
    }

    @Test
    void shouldUpdateTag() {

        UrlTagModel tag = new UrlTagModel();
        tag.setId(generator.nextId());

        UrlTagCdcEvent cdc =
                mock(UrlTagCdcEvent.class);

        TiCdcEvent<UrlTagCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_tags");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isUpdate()).thenReturn(true);
        when(event.firstData()).thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(tag);

        when(save.execute(tag))
                .thenReturn(
                        Result.success(
                                tag,
                                200
                        )
                );

        useCase.process(event);

        verify(save)
                .execute(tag);

        verify(redis)
                .save(
                        "url_tags:10:20",
                        "processed"
                );
    }

    @Test
    void shouldDeleteTag() {

        UrlTagCdcEvent old =
                mock(UrlTagCdcEvent.class);

        when(old.id())
                .thenReturn(999L);

        TiCdcEvent<UrlTagCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_tags");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isDelete()).thenReturn(true);
        when(event.old()).thenReturn(List.of(old));

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
                        "url_tags:10:20",
                        "processed"
                );
    }

    @Test
    void shouldNotSaveEventWhenInsertFails() {

        UrlTagModel tag = new UrlTagModel();
        tag.setId(generator.nextId());

        UrlTagCdcEvent cdc =
                mock(UrlTagCdcEvent.class);

        TiCdcEvent<UrlTagCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_tags");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert()).thenReturn(true);
        when(event.firstData()).thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(tag);

        when(insert.execute(tag))
                .thenReturn(
                        Result.failure(
                                500,
                                "error"
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
    void shouldNotDeleteWhenOldDataIsEmpty() {

        TiCdcEvent<UrlTagCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_tags");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isDelete()).thenReturn(true);
        when(event.old()).thenReturn(List.of());

        when(redis.exists(anyString()))
                .thenReturn(false);

        useCase.process(event);

        verify(delete, never())
                .execute(anyLong());

        verify(redis, never())
                .save(anyString(), anyString());
    }
}