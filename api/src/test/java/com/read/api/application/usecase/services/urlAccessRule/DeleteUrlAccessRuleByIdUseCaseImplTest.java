package com.read.api.application.usecase.services.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.DeleteUrlAccessRuleByIdUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteUrlAccessRuleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock private UrlAccessRuleRepository repository;

    @Mock private UrlRepository urlRepository;

    @InjectMocks
    private DeleteUrlAccessRuleByIdUseCaseImpl useCase;

    @Test
    void should_delete_rule_and_decrement_metric() {

        Long ruleId = 10L;
        Long urlId = 20L;

        UrlModel url = new UrlModel();

        url.getMetric().incrementAccessRuleCount();

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.of(urlId));

        when(repository.deleteById(ruleId))
                .thenReturn(1);

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        Result<Void> result =
                useCase.execute(ruleId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        assertThat(
                url.getMetric().getAccessRuleCount()
        ).isZero();

        InOrder order =
                inOrder(
                        repository,
                        urlRepository
                );

        order.verify(repository)
                .findUrlIdById(ruleId);

        order.verify(repository)
                .deleteById(ruleId);

        order.verify(urlRepository)
                .findById(urlId);

        order.verify(urlRepository)
                .save(url);

        verifyNoMoreInteractions(
                repository,
                urlRepository
        );
    }

    @Test
    void should_return_404_when_rule_not_found() {

        Long ruleId = 99L;

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.empty());

        Result<Void> result =
                useCase.execute(ruleId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Access Rule not found");

        verify(repository)
                .findUrlIdById(ruleId);

        verify(repository, never())
                .deleteById(anyLong());

        verifyNoInteractions(urlRepository);
    }

    @Test
    void should_return_404_when_delete_returns_zero() {

        Long ruleId = 10L;
        Long urlId = 20L;

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.of(urlId));

        when(repository.deleteById(ruleId))
                .thenReturn(0);

        Result<Void> result =
                useCase.execute(ruleId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);

        verify(repository)
                .findUrlIdById(ruleId);

        verify(repository)
                .deleteById(ruleId);

        verifyNoInteractions(urlRepository);
    }

    @Test
    void should_not_save_url_when_url_not_exists() {

        Long ruleId = 10L;
        Long urlId = 20L;

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.of(urlId));

        when(repository.deleteById(ruleId))
                .thenReturn(1);

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.empty());

        Result<Void> result =
                useCase.execute(ruleId);

        assertThat(result.isSuccess()).isTrue();

        verify(repository)
                .findUrlIdById(ruleId);

        verify(repository)
                .deleteById(ruleId);

        verify(urlRepository)
                .findById(urlId);

        verify(urlRepository, never())
                .save(any());

        verifyNoMoreInteractions(
                repository,
                urlRepository
        );
    }

    @Test
    void should_not_decrement_below_zero() {

        Long ruleId = 10L;
        Long urlId = 20L;

        UrlModel url = new UrlModel();

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.of(urlId));

        when(repository.deleteById(ruleId))
                .thenReturn(1);

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        Result<Void> result =
                useCase.execute(ruleId);

        assertThat(result.isSuccess()).isTrue();

        assertThat(
                url.getMetric().getAccessRuleCount()
        ).isZero();

        verify(urlRepository)
                .save(url);
    }
}