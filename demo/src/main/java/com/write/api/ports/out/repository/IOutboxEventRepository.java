package com.write.api.ports.out.repository;

import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.out.repository.shared.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface IOutboxEventRepository extends CrudRepository<OutboxEventModel, Long> {
    Optional<OutboxEventModel> findByAggregateId(Long aggregateId);
    List<OutboxEventModel> saveAll(List<OutboxEventModel> items);
    List<OutboxEventModel> findByStatus(OutboxStatusEnum status, int limit);
}
