package com.read.api.domain.repository;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.base.BaseRepository;

import java.util.List;

public interface UrlRedirectRuleRepository
        extends BaseRepository<UrlRedirectRuleModel, Long, UrlRedirectRuleFilter> {
    List<UrlRedirectRuleModel> findActiveRulesByUrlId(Long urlId);
}
