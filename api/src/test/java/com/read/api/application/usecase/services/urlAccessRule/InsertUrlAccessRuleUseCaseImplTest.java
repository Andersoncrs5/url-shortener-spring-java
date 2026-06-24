package com.read.api.application.usecase.services.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.InsertUrlAccessRuleUseCaseImpl;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InsertUrlAccessRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private InsertUrlAccessRuleUseCaseImpl useCase;

    @Test
    void should_insert_rule_and_increment_metric() {

        UrlModel url = new UrlModel();
        url.setId(100L);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setId(1L);
        rule.setUrlId(100L);
        rule.setRuleValue("admin@test.com");
        rule.setActive(true);

        when(urlRepository.findById(100L))
                .thenReturn(Optional.of(url));

        when(repository.insert(rule))
                .thenReturn(rule);

        Result<UrlAccessRuleModel> result =
                useCase.execute(rule);

        assertTrue(result.isSuccess());
        assertEquals(201, result.getStatusCode());

        assertNotNull(result.getValue());
        assertEquals(rule.getId(), result.getValue().getId());

        assertEquals(
                1L,
                url.getMetric().getAccessRuleCount()
        );

        verify(repository).insert(rule);
        verify(urlRepository).save(url);
    }

    @Test
    void should_return_failure_when_url_not_found() {

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setUrlId(999L);

        when(urlRepository.findById(999L))
                .thenReturn(Optional.empty());

        Result<UrlAccessRuleModel> result =
                useCase.execute(rule);

        assertFalse(result.isSuccess());

        assertEquals(
                404,
                result.getStatusCode()
        );

        assertEquals(
                "Url not found",
                result.getMessage()
        );

        verify(urlRepository)
                .findById(999L);

        verifyNoInteractions(repository);

        verify(
                urlRepository,
                never()
        ).save(any());
    }

    @Test
    void should_save_updated_url_after_metric_increment() {

        UrlModel url = new UrlModel();
        url.setId(10L);

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setUrlId(10L);

        when(urlRepository.findById(10L))
                .thenReturn(Optional.of(url));

        when(repository.insert(rule))
                .thenReturn(rule);

        useCase.execute(rule);

        ArgumentCaptor<UrlModel> captor =
                ArgumentCaptor.forClass(
                        UrlModel.class
                );

        verify(urlRepository)
                .save(captor.capture());

        UrlModel saved =
                captor.getValue();

        assertEquals(
                1L,
                saved.getMetric()
                        .getAccessRuleCount()
        );
    }

    @Test
    void should_execute_operations_in_correct_order() {

        UrlModel url =
                new UrlModel();

        url.setId(1L);

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setUrlId(1L);

        when(urlRepository.findById(1L))
                .thenReturn(Optional.of(url));

        when(repository.insert(rule))
                .thenReturn(rule);

        useCase.execute(rule);

        InOrder order =
                inOrder(
                        urlRepository,
                        repository
                );

        order.verify(urlRepository)
                .findById(1L);

        order.verify(repository)
                .insert(rule);

        order.verify(urlRepository)
                .save(url);
    }

    @Test
    void should_return_same_inserted_entity() {

        UrlModel url =
                new UrlModel();

        url.setId(1L);

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setId(99L);
        rule.setUrlId(1L);

        when(urlRepository.findById(1L))
                .thenReturn(Optional.of(url));

        when(repository.insert(rule))
                .thenReturn(rule);

        Result<UrlAccessRuleModel> result =
                useCase.execute(rule);

        assertSame(
                rule,
                result.getValue()
        );
    }

    @Test
    void should_increment_metric_only_once() {

        UrlModel url =
                new UrlModel();

        url.setId(1L);

        UrlAccessRuleModel rule =
                new UrlAccessRuleModel();

        rule.setUrlId(1L);

        when(urlRepository.findById(1L))
                .thenReturn(Optional.of(url));

        when(repository.insert(rule))
                .thenReturn(rule);

        useCase.execute(rule);

        assertEquals(
                1L,
                url.getMetric()
                        .getAccessRuleCount()
        );
    }
}