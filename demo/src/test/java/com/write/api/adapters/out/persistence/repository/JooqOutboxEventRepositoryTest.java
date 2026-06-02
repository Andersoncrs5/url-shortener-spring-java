package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
class JooqOutboxEventRepositoryTest {

    @Autowired
    private HelpRepositoryTest help;

    @Autowired
    private SnowflakeIdGenerator generator;

    @Autowired
    private JooqOutboxEventRepository repository;

    private OutboxEventModel event;

    @BeforeEach
    void setup() {
        event = new OutboxEventModel();
        event.setAggregateType(AggregateTypeEnum.USER);
        event.setAggregateId(generator.nextId());
        event.setEventType(EventTypeEnum.CREATED);
        event.setPayload("""
                {
                  "id": 1,
                  "name": "john",
                  "email": "john@test.com"
                }
                """);
        event.setStatus(OutboxStatusEnum.PENDING);
        event.setRetryCount(0);
        event.setErrorMessage(null);
        event.setNextRetryAt(LocalDateTime.now().plusMinutes(5));
        event.setProcessedAt(null);
    }

    @Test
    void shouldInsertOutboxEvent() {
        OutboxEventModel saved = help.createOutBox();
    }

    @Test
    void shouldUpdateOutboxEvent() {
        OutboxEventModel saved = repository.insert(event);

        saved.setStatus(OutboxStatusEnum.PROCESSING);
        saved.setRetryCount(2);
        saved.setErrorMessage("some error");
        saved.setProcessedAt(LocalDateTime.now());
        saved.setNextRetryAt(LocalDateTime.now().plusMinutes(10));
        saved.setPayload("""
                {
                  "id": 1,
                  "name": "john updated",
                  "email": "john.updated@test.com"
                }
                """);

        OutboxEventModel updated = repository.save(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getStatus()).isEqualTo(OutboxStatusEnum.PROCESSING);
        assertThat(updated.getRetryCount()).isEqualTo(2);
        assertThat(updated.getErrorMessage()).isEqualTo("some error");
        assertThat(updated.getProcessedAt()).isNotNull();
        assertThat(updated.getNextRetryAt()).isNotNull();
    }

    @Test
    void shouldDeleteOutboxEventById() {
        OutboxEventModel saved = repository.insert(event);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);

        Optional<OutboxEventModel> result = repository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindOutboxEventById() {
        OutboxEventModel saved = repository.insert(event);

        Optional<OutboxEventModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();

        OutboxEventModel found = result.get();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getAggregateType()).isEqualTo(saved.getAggregateType());
        assertThat(found.getAggregateId()).isEqualTo(saved.getAggregateId());
        assertThat(found.getEventType()).isEqualTo(saved.getEventType());
        assertThat(found.getStatus()).isEqualTo(saved.getStatus());
        assertThat(found.getPayload()).isNotBlank();
    }

    @Test
    void shouldReturnEmptyWhenOutboxEventNotFound() {
        Optional<OutboxEventModel> result = repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenOutboxEventExistsById() {
        OutboxEventModel saved = repository.insert(event);

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenOutboxEventDoesNotExistById() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldSaveWhenIdIsNull() {
        event.setId(null);

        OutboxEventModel saved = repository.save(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldNotAllowDuplicateInsertWithSameIdWhenUpdatingNonExistingEvent() {
        OutboxEventModel saved = repository.insert(event);

        saved.setId(generator.nextId());
        saved.setStatus(OutboxStatusEnum.FAILED);
        saved.setErrorMessage("forced update");

        assertThatThrownBy(() -> repository.save(saved))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Outbox event not found");
    }

    @Test
    void shouldKeepPayloadAsJsonString() {
        OutboxEventModel saved = repository.insert(event);

        Optional<OutboxEventModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getPayload()).contains("\"name\"");
        assertThat(result.get().getPayload()).contains("\"email\"");
    }
}