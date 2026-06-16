package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.domain.model.UrlAccessRuleModel;

import java.util.List;

public interface FindAllUrlAccessRuleByUrlIdUseCase {
    List<UrlAccessRuleModel> execute(Long urlId);
}
