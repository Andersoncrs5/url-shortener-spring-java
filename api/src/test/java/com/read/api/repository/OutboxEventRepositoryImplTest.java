package com.read.api.repository;

import com.read.api.api.dto.outbox.OutboxEventFilter;
import com.read.api.domain.enums.OutboxStatusEnum;
import com.read.api.domain.model.OutboxEventModel;
import com.read.api.infrastructure.persistence.entity.OutboxEventEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class OutboxEventRepositoryImplTest extends BaseRepositoryTest {

    @BeforeEach
    void setup() {
        template.dropCollection(OutboxEventEntity.class);
    }

    @Test
    void should_save_outbox_event() {
        createOutboxEvent();
    }

    @Test
    void should_find_outbox_event_by_id() {

        OutboxEventModel saved = createOutboxEvent();

        var found = outboxEventRepository.findById(
                saved.getId()
        );

        assertTrue(found.isPresent());
        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_verify_if_outbox_event_exists() {

        OutboxEventModel saved = createOutboxEvent();

        assertTrue(
                outboxEventRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_delete_outbox_event() {

        OutboxEventModel saved = createOutboxEvent();

        int deleted = outboxEventRepository.deleteById(
                saved.getId()
        );

        assertEquals(1, deleted);

        assertFalse(
                outboxEventRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_find_outbox_events_by_status_filter() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setStatus(saved.getStatus());

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_outbox_events_by_aggregate_id_filter() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setAggregateId(
                saved.getAggregateId()
        );

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_outbox_events_by_event_type_filter() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setEventType(
                saved.getEventType()
        );

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_outbox_events_by_topic_filter() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setTopic(
                saved.getTopic()
        );

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_outbox_events_by_payload_filter() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setPayload("user");

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertThat(
                page.getContent()
        ).isNotEmpty();
    }

    @Test
    void should_find_outbox_events_by_retry_count_range() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setRetryCountMin(
                saved.getRetryCount()
        );

        filter.setRetryCountMax(
                saved.getRetryCount()
        );

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_outbox_events_by_error_message_filter() {

        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter =
                new OutboxEventFilter();

        filter.setErrorMessage(
                saved.getErrorMessage()
        );

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_outbox_events_by_next_retry_at_range() {
        OutboxEventModel saved = createOutboxEvent();

        saved.setNextRetryAt(LocalDateTime.now().plusMinutes(10));
        outboxEventRepository.save(saved);

        OutboxEventFilter filter = new OutboxEventFilter();
        filter.setNextRetryAtAfter(saved.getNextRetryAt().minusMinutes(1));
        filter.setNextRetryAtBefore(saved.getNextRetryAt().plusMinutes(1));

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_outbox_events_by_processed_at_range() {
        OutboxEventModel saved = createOutboxEvent();

        saved.setProcessedAt(LocalDateTime.now().minusMinutes(5));
        outboxEventRepository.save(saved);

        OutboxEventFilter filter = new OutboxEventFilter();
        filter.setProcessedAtAfter(saved.getProcessedAt().minusMinutes(1));
        filter.setProcessedAtBefore(saved.getProcessedAt().plusMinutes(1));

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_outbox_events_by_aggregate_type_filter() {
        OutboxEventModel saved = createOutboxEvent();

        OutboxEventFilter filter = new OutboxEventFilter();
        filter.setAggregateType(saved.getAggregateType());

        var page = outboxEventRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
    }

}
