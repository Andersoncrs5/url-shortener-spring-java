package com.read.api.domain.repository;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.base.BaseRepository;
import com.read.api.utils.validation.isId.IsId;

import java.util.List;
import java.util.Optional;

public interface UrlRedirectRuleRepository
        extends BaseRepository<UrlRedirectRuleModel, Long, UrlRedirectRuleFilter> {
    List<UrlRedirectRuleModel> findActiveRulesByUrlId(@IsId Long urlId);

    Optional<Long> findUrlIdById(@IsId Long id);
}

