package com.read.api.application.usecase.impl.urlRedirectRule;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.application.usecase.interfaces.urlRedirectRule.FindAllFilterUrlRedirectRuleUseCase;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllFilterUrlRedirectRuleUseCaseImpl implements FindAllFilterUrlRedirectRuleUseCase {
    UrlRedirectRuleRepository repository;

    @Override
    public Page<UrlRedirectRuleModel> execute(UrlRedirectRuleFilter filer, Pageable pageable) {
        return repository.findAll(filer, pageable);
    }
}
