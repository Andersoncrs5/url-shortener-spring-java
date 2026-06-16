package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.interfaces.urlAccessRule.ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCaseImpl
        implements ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase {
    UrlAccessRuleRepository repository;

    @Override
    public boolean execute(Long urlId, UrlAccessRuleTypeEnum type, String ruleValue) {
        return repository.existsUnique(urlId, type, ruleValue);
    }

}
