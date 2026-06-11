package com.read.api.domain.repository;

import com.read.api.api.dto.outbox.OutboxEventFilter;
import com.read.api.domain.model.OutboxEventModel;
import com.read.api.domain.repository.base.BaseRepository;

public interface OutboxEventRepository extends BaseRepository<OutboxEventModel, Long, OutboxEventFilter> {
}
