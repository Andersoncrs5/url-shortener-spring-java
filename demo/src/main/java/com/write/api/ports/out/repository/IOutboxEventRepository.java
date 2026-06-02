package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.out.repository.shared.CrudRepository;

public interface IOutboxEventRepository extends CrudRepository<OutboxEventModel, Long> {
}
