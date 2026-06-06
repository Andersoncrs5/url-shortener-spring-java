package com.write.api.ports.out.repository;

import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.out.repository.shared.CrudRepository;
import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;
import java.util.List;

public interface IUrlRepository extends CrudRepository<UrlModel, Long> {
    boolean existsByShortCode(String code);
    boolean existsByUserIdAndUrlId(@IsId Long userId, @IsId Long urlId);
    List<UrlModel> findToDelete(UrlStatusEnum status, int limit, LocalDateTime deletedAt);
}
