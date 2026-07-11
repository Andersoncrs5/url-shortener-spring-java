package com.notify.notify.configs.kafka.dlq;


import com.notify.notify.globals.enums.TopicEnum;

public interface DeadLetterPublisher {

    <T> void publish(
            TopicEnum topic,
            T payload,
            Throwable error
    );

}
