package com.write.api.ports.out.messaging;

import com.write.api.core.domain.model.OutboxEventModel;
import org.springframework.kafka.support.SendResult;

public interface OutboxEventPublisher {

    SendResult<String, String> publish(OutboxEventModel event);

}