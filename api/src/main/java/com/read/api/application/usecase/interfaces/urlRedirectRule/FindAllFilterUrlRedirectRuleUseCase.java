package com.read.api.application.usecase.interfaces.urlRedirectRule;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.domain.model.UrlRedirectRuleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllFilterUrlRedirectRuleUseCase {
    Page<UrlRedirectRuleModel> execute(UrlRedirectRuleFilter filer, Pageable pageable);
}
