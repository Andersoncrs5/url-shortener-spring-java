package com.read.api.domain.repository;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.base.BaseRepository;

import java.util.List;

public interface UrlAccessRuleRepository extends BaseRepository<UrlAccessRuleModel, Long, UrlAccessRuleFilter> {
    List<UrlAccessRuleModel> findAllByUrlId(Long urlId);
    boolean existsUnique(Long urlId, UrlAccessRuleTypeEnum type, String ruleValue);
}
