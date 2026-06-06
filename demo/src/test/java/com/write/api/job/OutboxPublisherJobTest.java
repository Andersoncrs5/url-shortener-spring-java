package com.write.api.job;

import com.write.api.infrastructure.scheduler.OutboxPublisherJob;
import com.write.api.ports.in.outbox.PublishPendingOutboxEventsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherJobTest {

    @Mock
    private PublishPendingOutboxEventsUseCase useCase;

    @InjectMocks
    private OutboxPublisherJob job;

    @Test
    void shouldExecutePublishPendingEvents() {
        job.publishPendingEvents();

        verify(useCase).execute();
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void shouldPropagateExceptionWhenUseCaseFails() {
        RuntimeException exception =
                new RuntimeException("Kafka unavailable");

        doThrow(exception)
                .when(useCase)
                .execute();

        assertThatThrownBy(() -> job.publishPendingEvents())
                .isSameAs(exception)
                .hasMessage("Kafka unavailable");

        verify(useCase).execute();
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void shouldExecuteOnlyOncePerInvocation() {
        job.publishPendingEvents();

        verify(useCase, times(1)).execute();
        verifyNoMoreInteractions(useCase);
    }
}