package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.ports.out.repository.shared.CrudRepository;
import com.write.api.shared.validation.snowflake.IsId;

public interface IUrlAccessRuleRepository extends CrudRepository<UrlAccessRuleModel, Long> {
    int countByUrlId(@IsId Long id);
}
