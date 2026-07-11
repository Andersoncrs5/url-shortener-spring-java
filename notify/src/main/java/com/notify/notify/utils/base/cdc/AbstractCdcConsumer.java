package com.notify.notify.utils.base.cdc;

import com.notify.notify.configs.kafka.dlq.DeadLetterPublisher;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.enums.TopicEnum;
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
