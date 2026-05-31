package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.out.repository.shared.CrudRepository;
import com.write.api.shared.validation.snowflake.IsId;

public interface IUrlRepository extends CrudRepository<UrlModel, Long> {
    boolean existsByShortCode(String code);
    boolean existsByUserIdAndUrlId(@IsId Long userId, @IsId Long urlId);
}
