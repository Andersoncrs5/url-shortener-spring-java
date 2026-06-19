package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.ports.out.repository.shared.CrudRepository;
import com.write.api.shared.validation.snowflake.IsId;

public interface IUrlTagLinkRepository extends CrudRepository<UrlTagLinkModel, Long> {
    int countByUrlId(@IsId Long id);
}
