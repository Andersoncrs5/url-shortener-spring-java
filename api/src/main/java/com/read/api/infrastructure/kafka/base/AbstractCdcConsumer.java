package com.read.api.infrastructure.kafka.base;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.infrastructure.kafka.dlq.DeadLetterPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCdcConsumer<T> {

    protected final DeadLetterPublisher deadLetterPublisher;

    protected AbstractCdcConsumer(
            DeadLetterPublisher deadLetterPublisher
    ) {
        this.deadLetterPublisher = deadLetterPublisher;
    }

    protected void process(
            TiCdcEvent<T> event,
            Runnable action,
            TopicEnum dlq
    ) {

        try {

            action.run();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

            deadLetterPublisher.publish(
                    dlq,
                    event,
                    ex
            );
        }
    }
}