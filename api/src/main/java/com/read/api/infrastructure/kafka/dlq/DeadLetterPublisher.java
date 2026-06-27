package com.read.api.infrastructure.kafka.dlq;

import com.read.api.domain.enums.TopicEnum;

public interface DeadLetterPublisher {

    <T> void publish(
            TopicEnum topic,
            T payload,
            Throwable error
    );

}