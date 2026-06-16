package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.domain.enums.UrlAccessRuleTypeEnum;

public interface ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase {
    boolean execute(
            Long urlId,
            UrlAccessRuleTypeEnum type,
            String ruleValue
    );
}
