package com.write.api.application.service.urlAccessRule;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.urlAccessRule.UrlAccessRuleCreatedEvent;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteUrlAccessRuleServiceTest extends BaseServiceTest {

    @Mock
    private IUrlAccessRuleRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private DeleteUrlAccessRuleService service;

    private UrlAccessRuleModel rule;

    @BeforeEach
    void setup() {
        rule = new UrlAccessRuleModel();
        rule.setId(1L);
        rule.setUrlId(100L);
        rule.setAssignedByUserId(10L);
        rule.setRuleValue("ADMIN");
        rule.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldDeleteUrlAccessRuleSuccessfully() {
        when(repository.findById(rule.getId())).thenReturn(Optional.of(rule));
        when(repository.deleteById(rule.getId())).thenReturn(1);
        when(outbox.execute(any(CreateOutboxEventCommand.class))).thenReturn(Result.success());

        Result<Void> result = service.execute(rule.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        ArgumentCaptor<CreateOutboxEventCommand> captor = ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        InOrder order = inOrder(repository, outbox);
        order.verify(repository).findById(rule.getId());
        order.verify(repository).deleteById(rule.getId());
        order.verify(outbox).execute(captor.capture());

        CreateOutboxEventCommand command = captor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.URL_ACCESS_RULE);
        assertThat(command.aggregateId()).isEqualTo(rule.getId());
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.URL_ACCESS_RULE_DELETED);
        assertThat(command.topic()).isEqualTo(TopicEnum.URL_ACCESS_RULE_DELETED);

        verifyNoMoreInteractions(repository, outbox);
    }

    @Test
    void shouldReturn404WhenRuleDoesNotExist() {
        when(repository.findById(rule.getId())).thenReturn(Optional.empty());

        Result<Void> result = service.execute(rule.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url Access Rule not found");

        verify(repository).findById(rule.getId());
        verifyNoInteractions(outbox);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void shouldFailWhenOutboxCreationFails() {
        when(repository.findById(rule.getId())).thenReturn(Optional.of(rule));
        when(repository.deleteById(rule.getId())).thenReturn(1);
        when(outbox.execute(any(CreateOutboxEventCommand.class))).thenReturn(Result.failure(500, "Failed to create outbox event"));

        Result<Void> result = service.execute(rule.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).isEqualTo("Failed to create outbox event");

        verify(repository).findById(rule.getId());
        verify(repository).deleteById(rule.getId());
        verify(outbox).execute(any(CreateOutboxEventCommand.class));
    }

    @Test
    void shouldReturn404WhenDeleteReturnsZero() {
        when(repository.findById(rule.getId())).thenReturn(Optional.of(rule));
        when(repository.deleteById(rule.getId())).thenReturn(0);

        Result<Void> result = service.execute(rule.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url Access Rule not found");

        verify(repository).findById(rule.getId());
        verify(repository).deleteById(rule.getId());
        verifyNoInteractions(outbox);
    }
}