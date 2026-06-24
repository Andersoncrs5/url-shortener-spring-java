package com.read.api.application.usecase.cdc;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.cdc.urlAccessRule.UrlAccessRuleCdcMapper;
import com.read.api.application.usecase.impl.cdc.urlAccessRule.UrlAccessRuleCdcServiceUseImpl;
import com.read.api.application.usecase.impl.urlAccessRule.DeleteUrlAccessRuleByIdUseCaseImpl;
import com.read.api.application.usecase.impl.urlAccessRule.InsertUrlAccessRuleUseCaseImpl;
import com.read.api.application.usecase.impl.urlAccessRule.SaveUrlAccessRuleUseCaseImpl;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlAccessRuleCdcEvent;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UrlAccessRuleCdcServiceUseImplTest extends BaseUseCaseTest {
    @Mock
    private InsertUrlAccessRuleUseCaseImpl insert;

    @Mock
    private SaveUrlAccessRuleUseCaseImpl save;

    @Mock
    private DeleteUrlAccessRuleByIdUseCaseImpl delete;

    @Mock
    private UrlAccessRuleCdcMapper mapper;

    @InjectMocks
    private UrlAccessRuleCdcServiceUseImpl useCase;

    @Test
    void shouldIgnoreDuplicateEvent() {

        TiCdcEvent<UrlAccessRuleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_access_rule");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(redis.exists("url_access_rule:10:20"))
                .thenReturn(true);

        useCase.process(event);

        verify(redis)
                .exists("url_access_rule:10:20");

        verifyNoInteractions(
                insert,
                save,
                delete,
                mapper
        );
    }

    @Test
    void shouldInsertRule() {

        UrlAccessRuleModel rule = createUrlAccessRule();

        UrlAccessRuleCdcEvent cdc = mock(UrlAccessRuleCdcEvent.class);

        TiCdcEvent<UrlAccessRuleCdcEvent> event = mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_access_rule");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(rule);

        when(insert.execute(rule))
                .thenReturn(
                        Result.success(
                                rule,
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
                .exists("url_access_rule:10:20");

        order.verify(mapper)
                .toModel(cdc);

        order.verify(insert)
                .execute(rule);

        order.verify(redis)
                .save(
                        "url_access_rule:10:20",
                        "processed"
                );
    }

    @Test
    void shouldUpdateRule() {

        UrlAccessRuleModel rule =
                createUrlAccessRule();

        UrlAccessRuleCdcEvent cdc =
                mock(UrlAccessRuleCdcEvent.class);

        TiCdcEvent<UrlAccessRuleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_access_rule");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isUpdate())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(rule);

        when(save.execute(rule))
                .thenReturn(
                        Result.success(
                                rule,
                                200
                        )
                );

        useCase.process(event);

        verify(save)
                .execute(rule);

        verify(redis)
                .save(
                        "url_access_rule:10:20",
                        "processed"
                );
    }

    @Test
    void shouldDeleteRule() {

        UrlAccessRuleCdcEvent old =
                mock(UrlAccessRuleCdcEvent.class);

        when(old.id())
                .thenReturn(999L);

        TiCdcEvent<UrlAccessRuleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_access_rule");
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
                        "url_access_rule:10:20",
                        "processed"
                );
    }

    @Test
    void shouldNotSaveEventWhenInsertFails() {

        UrlAccessRuleModel rule =
                createUrlAccessRule();

        UrlAccessRuleCdcEvent cdc =
                mock(UrlAccessRuleCdcEvent.class);

        TiCdcEvent<UrlAccessRuleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_access_rule");
        when(event.ts()).thenReturn(10L);
        when(event.es()).thenReturn(20L);

        when(event.isInsert())
                .thenReturn(true);

        when(event.firstData())
                .thenReturn(cdc);

        when(redis.exists(anyString()))
                .thenReturn(false);

        when(mapper.toModel(cdc))
                .thenReturn(rule);

        when(insert.execute(rule))
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
    void shouldNotDeleteWhenOldDataIsEmpty() {

        TiCdcEvent<UrlAccessRuleCdcEvent> event =
                mock(TiCdcEvent.class);

        when(event.table()).thenReturn("url_access_rule");
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
