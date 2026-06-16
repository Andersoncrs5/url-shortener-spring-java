package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.model.UrlAccessRuleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllFilterUrlAccessRuleUseCase {
    Page<UrlAccessRuleModel> execute(UrlAccessRuleFilter filter, Pageable pageable);
}
