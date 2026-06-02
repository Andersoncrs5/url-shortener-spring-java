package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.out.repository.shared.CrudRepository;

import java.util.Optional;

public interface IApiKeyRepository extends CrudRepository<ApiKeyModel, Long> {
    Optional<ApiKeyModel> findByKeyHash(String keyHash);
}
