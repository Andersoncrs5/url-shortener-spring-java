package com.read.api.application.usecase.urlRedirectRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.InsertUrlRedirectRuleUseCaseImpl;
import com.read.api.domain.model.UrlModel; // Adicionado
import com.read.api.domain.model.metrics.UrlMetricModel; // Adicionado se necessário
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository; // Adicionado
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InsertUrlRedirectRuleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private InsertUrlRedirectRuleUseCaseImpl useCase;

    @Test
    void should_insert_url_redirect_rule() {

        UrlModel urlMock = new UrlModel();
        urlMock.setId(100L);

        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(1L);
        rule.setUrlId(100L);
        rule.setCountryCode("BR");
        rule.setRedirectUrl("https://google.com");
        rule.setActive(true);

        // Mocks de comportamento
        when(urlRepository.findById(100L)).thenReturn(Optional.of(urlMock));
        when(repository.insert(rule)).thenReturn(rule);

        // Execução
        Result<UrlRedirectRuleModel> result = useCase.execute(rule);

        // Asserções
        assertTrue(result.isSuccess());
        assertEquals(201, result.getStatusCode());
        assertNotNull(result.getValue());
        assertEquals(rule.getId(), result.getValue().getId());
        assertEquals(rule.getUrlId(), result.getValue().getUrlId());
        assertEquals(rule.getRedirectUrl(), result.getValue().getRedirectUrl());

        // Verificações de chamada
        verify(urlRepository).findById(100L);
        verify(repository).insert(rule);
        verify(urlRepository).save(urlMock); // Garante que a métrica modificada foi salva
    }

    @Test
    void should_return_inserted_entity() {
        UrlModel urlMock = new UrlModel();
        urlMock.setId(99L);

        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(99L);
        rule.setUrlId(99L);

        when(urlRepository.findById(99L)).thenReturn(Optional.of(urlMock));
        when(repository.insert(rule)).thenReturn(rule);

        Result<UrlRedirectRuleModel> result = useCase.execute(rule);

        assertSame(rule, result.getValue());
        verify(repository).insert(rule);
    }
}