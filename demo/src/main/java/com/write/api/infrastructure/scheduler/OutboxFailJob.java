package com.write.api.infrastructure.scheduler;

import com.write.api.ports.in.outbox.PublishFailedOutboxEventsUseCase;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OutboxFailJob {
    PublishFailedOutboxEventsUseCase useCase;

    @Scheduled(
            fixedDelay = 5,
            timeUnit = TimeUnit.MINUTES
    )
    @Bulkhead(name = "outbox")
    public void publishPendingEvents() {
        useCase.execute();
    }

}
