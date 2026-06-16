package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllFilterUrlAccessRuleUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
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
public class FindAllFilterUrlAccessRuleUseCaseImpl implements FindAllFilterUrlAccessRuleUseCase {
    UrlAccessRuleRepository repository;

    @Override
    public Page<UrlAccessRuleModel> execute(UrlAccessRuleFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }
}
