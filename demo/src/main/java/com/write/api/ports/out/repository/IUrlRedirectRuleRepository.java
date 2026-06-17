package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.ports.out.repository.shared.CrudRepository;

public interface IUrlRedirectRuleRepository extends CrudRepository<UrlRedirectRuleModel, Long> {
    int countByUrlId(Long urlId);
}
