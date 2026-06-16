package com.read.api.application.usecase.urlAccessRule;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.urlAccessRule.FindAllUrlAccessRuleByUrlIdUseCaseImpl;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FindAllUrlAccessRuleByUrlIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlAccessRuleRepository repository;

    @InjectMocks
    private FindAllUrlAccessRuleByUrlIdUseCaseImpl useCase;

    @Test
    void should_find_all_url_access_rules_by_url_id() {

        Long urlId = 1L;

        UrlAccessRuleModel rule1 = createUrlAccessRule();
        rule1.setUrlId(urlId);

        UrlAccessRuleModel rule2 = createUrlAccessRule();
        rule2.setUrlId(urlId);

        when(repository.findAllByUrlId(urlId))
                .thenReturn(List.of(rule1, rule2));

        List<UrlAccessRuleModel> result =
                useCase.execute(urlId);

        assertThat(result)
                .hasSize(2)
                .containsExactly(rule1, rule2);

        verify(repository)
                .findAllByUrlId(urlId);
    }

    @Test
    void should_return_empty_list_when_no_rules_exist() {

        Long urlId = 999L;

        when(repository.findAllByUrlId(urlId))
                .thenReturn(List.of());

        List<UrlAccessRuleModel> result =
                useCase.execute(urlId);

        assertThat(result).isEmpty();

        verify(repository)
                .findAllByUrlId(urlId);
    }
}