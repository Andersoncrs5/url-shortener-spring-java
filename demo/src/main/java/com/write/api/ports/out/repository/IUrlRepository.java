package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UrlModel;

public interface IUrlRepository extends CrudRepository<UrlModel, Long> {
    boolean existsByShortCode(String code);
}
