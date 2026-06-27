package com.read.api.application.usecase.interfaces.deadLetterEvent;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.domain.model.DeadLetterEventModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllDeadLetterEventUseCase {
    Page<DeadLetterEventModel> execute(DeadLetterEventFilter filter, Pageable pageable);
}
