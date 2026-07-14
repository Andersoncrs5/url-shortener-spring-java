package com.notify.notify.utils.base.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.globals.classes.outbox.OutboxCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.services.redis.RedisCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import java.time.Duration;

public abstract class BaseConsumer {
    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected RedisCrudService cache;

    public Result<Void> check(OutboxCdcEvent outboxEvent) {
        String key = "outbox:lock:" + outboxEvent.eventId();
        boolean exists = this.cache.exists(key);

        if (exists) {
            return Result.success(HttpStatus.ACCEPTED);
        }

        this.cache.save(key, "processing", Duration.ofDays(3));

        return Result.success(HttpStatus.CREATED);
    }

    public void invalidateLock(OutboxCdcEvent outboxEvent) {
        this.cache.delete("outbox:lock:" + outboxEvent.eventId());
    }
}