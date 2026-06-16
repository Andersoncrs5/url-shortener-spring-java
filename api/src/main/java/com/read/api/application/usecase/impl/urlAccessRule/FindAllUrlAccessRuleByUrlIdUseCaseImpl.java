package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllUrlAccessRuleByUrlIdUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllUrlAccessRuleByUrlIdUseCaseImpl implements FindAllUrlAccessRuleByUrlIdUseCase {
    UrlAccessRuleRepository repository;

    @Override
    public List<UrlAccessRuleModel> execute(Long urlId) {
        return repository.findAllByUrlId(urlId);
    }
}
