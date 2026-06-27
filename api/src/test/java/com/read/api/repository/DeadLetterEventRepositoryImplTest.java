package com.read.api.repository;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.infrastructure.persistence.entity.DeadLetterEventEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class DeadLetterEventRepositoryImplTest extends BaseRepositoryTest {

    @BeforeEach
    void setup() {
        template.dropCollection(
                DeadLetterEventEntity.class
        );
    }

    @Test
    void should_save_dead_letter_event() {
        createDeadLetterEvent();
    }

    @Test
    void should_find_dead_letter_event_by_id() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        var found =
                deadLetterEventRepository.findById(
                        saved.getId()
                );

        assertTrue(found.isPresent());

        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_verify_if_dead_letter_event_exists() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        assertTrue(
                deadLetterEventRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_delete_dead_letter_event() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        int deleted =
                deadLetterEventRepository.deleteById(
                        saved.getId()
                );

        assertEquals(1, deleted);

        assertFalse(
                deadLetterEventRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_find_by_status_filter() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        DeadLetterEventFilter filter = new DeadLetterEventFilter();

        filter.setStatus(
                saved.getStatus()
        );

        var page =
                deadLetterEventRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_event_id_filter() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        DeadLetterEventFilter filter =
                new DeadLetterEventFilter();

        filter.setEventId(
                saved.getEventId()
        );

        var page =
                deadLetterEventRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_source_topic_filter() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        DeadLetterEventFilter filter =
                new DeadLetterEventFilter();

        filter.setSourceTopic(
                saved.getSourceTopic()
        );

        var page =
                deadLetterEventRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_error_message_filter() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        DeadLetterEventFilter filter =
                new DeadLetterEventFilter();

        filter.setErrorMessage(
                saved.getErrorMessage()
        );

        var page =
                deadLetterEventRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_retry_count_range() {

        DeadLetterEventModel saved =
                createDeadLetterEvent();

        DeadLetterEventFilter filter =
                new DeadLetterEventFilter();

        filter.setRetryCountMin(0);
        filter.setRetryCountMax(0);

        var page =
                deadLetterEventRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_top100_by_status_and_retry_count() {

        createDeadLetterEvent();

        var events =
                deadLetterEventRepository
                        .findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
                                DeadLetterStatus.PENDING,
                                5
                        );

        assertThat(events)
                .isNotEmpty();
    }

    @Test
    void should_find_pending_retry_events() {

        DeadLetterEventModel event =
                createDeadLetterEvent();

        event.setRetryCount(1);

        event.setNextRetryAt(
                LocalDateTime.now().minusMinutes(1)
        );

        deadLetterEventRepository.save(
                event
        );

        var events =
                deadLetterEventRepository
                        .findPendingRetryEvents(
                                LocalDateTime.now(),
                                PageRequest.of(0, 10)
                        );

        assertThat(events)
                .isNotEmpty();
    }
}