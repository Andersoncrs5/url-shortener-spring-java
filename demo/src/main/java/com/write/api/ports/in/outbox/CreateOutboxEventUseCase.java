package com.write.api.ports.in.outbox;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.OutboxEventModel;

public interface CreateOutboxEventUseCase {
    Result<OutboxEventModel> execute(
            CreateOutboxEventCommand command
    );
}