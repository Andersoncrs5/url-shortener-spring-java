package com.read.api.domain.repository;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.base.BaseRepository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeadLetterEventRepository
        extends BaseRepository<DeadLetterEventModel, Long, DeadLetterEventFilter> {
    Optional<DeadLetterEventModel> findByEventId(Long id);

    List<DeadLetterEventModel> findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
            DeadLetterStatus status,
            Integer maxRetries
    );

    List<DeadLetterEventModel> findPendingRetryEvents(
            LocalDateTime now,
            Pageable pageable
    );
}
