package com.read.api.application.usecase.urlRedirectRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlRedirectRule.DeleteUrlRedirectRuleByIdUseCaseImpl;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class DeleteUrlRedirectRuleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRedirectRuleRepository repository;

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private DeleteUrlRedirectRuleByIdUseCaseImpl useCase;

    @Test
    void should_delete_url_redirect_rule_and_decrement_metric() {
        Long ruleId = 1L;
        Long urlId = 2L;

        UrlModel url = new UrlModel();
        url.getMetric().incrementRedirectRuleCount();

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.of(urlId));

        when(repository.deleteById(ruleId))
                .thenReturn(1);

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.of(url));

        Result<Void> result = useCase.execute(ruleId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNull();

        assertThat(url.getMetric().getRedirectRuleCount()).isZero();

        InOrder order = inOrder(repository, urlRepository);
        order.verify(repository).findUrlIdById(ruleId);
        order.verify(repository).deleteById(ruleId);
        order.verify(urlRepository).findById(urlId);
        order.verify(urlRepository).save(url);

        verifyNoMoreInteractions(repository, urlRepository);
    }

    @Test
    void should_return_404_when_rule_id_not_found_in_repository() {
        Long ruleId = 999L;

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.empty());

        Result<Void> result = useCase.execute(ruleId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url Redirect Rule not found");

        verify(repository).findUrlIdById(ruleId);
        verify(repository, never()).deleteById(anyLong());
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

        Result<Void> result = useCase.execute(ruleId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url Redirect Rule not found");

        verify(repository).findUrlIdById(ruleId);
        verify(repository).deleteById(ruleId);
        verifyNoInteractions(urlRepository);
    }

    @Test
    void should_not_save_url_when_url_does_not_exist() {
        Long ruleId = 10L;
        Long urlId = 20L;

        when(repository.findUrlIdById(ruleId))
                .thenReturn(Optional.of(urlId));

        when(repository.deleteById(ruleId))
                .thenReturn(1);

        when(urlRepository.findById(urlId))
                .thenReturn(Optional.empty());

        Result<Void> result = useCase.execute(ruleId);

        assertThat(result.isSuccess()).isTrue();

        verify(repository).findUrlIdById(ruleId);
        verify(repository).deleteById(ruleId);
        verify(urlRepository).findById(urlId);
        verify(urlRepository, never()).save(any());

        verifyNoMoreInteractions(repository, urlRepository);
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

        Result<Void> result = useCase.execute(ruleId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(url.getMetric().getRedirectRuleCount()).isZero();

        verify(urlRepository).save(url);
    }
}