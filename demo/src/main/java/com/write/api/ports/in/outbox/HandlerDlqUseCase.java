package com.write.api.ports.in.outbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.shared.Result;

public interface HandlerDlqUseCase {
    <T> Result<Void> execute(
            String payload,
            TypeReference<OutboxEventMessage<T>> typeRef
    );
}
