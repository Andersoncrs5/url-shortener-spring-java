package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteUrlRedirectRuleServiceTest extends BaseServiceTest {

    @Mock
    private IUrlRedirectRuleRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private DeleteUrlRedirectRuleService service;

    private final Long id = 123L;

    private UrlRedirectRuleModel createRule() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(id);
        rule.setUrlId(100L);
        rule.setCreatedAt(LocalDateTime.now());
        return rule;
    }

    @Test
    void shouldDeleteUrlRedirectRuleSuccessfully() {

        UrlRedirectRuleModel rule = createRule();

        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        when(repository.deleteById(id))
                .thenReturn(1);

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success());

        Result<Void> result = service.execute(id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode())
                .isEqualTo(200);

        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);


        verify(repository).findById(id);
        verify(repository).deleteById(id);
        verify(outbox).execute(captor.capture());


        CreateOutboxEventCommand command = captor.getValue();

        assertThat(command.aggregateId())
                .isEqualTo(id);

        assertThat(command.payload())
                .isNotNull();


        verifyNoMoreInteractions(repository, outbox);
    }


    @Test
    void shouldReturn404WhenUrlRedirectRuleDoesNotExist() {

        when(repository.findById(id))
                .thenReturn(Optional.empty());


        Result<Void> result = service.execute(id);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(404);


        verify(repository)
                .findById(id);

        verify(repository, never())
                .deleteById(any());

        verifyNoInteractions(outbox);

        verifyNoMoreInteractions(repository);
    }


    @Test
    void shouldReturn404WhenDeleteReturnsZero() {

        UrlRedirectRuleModel rule = createRule();


        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        when(repository.deleteById(id))
                .thenReturn(0);


        Result<Void> result = service.execute(id);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(404);

        assertThat(result.getMessage())
                .isEqualTo("Url Rule not found");


        verify(repository)
                .findById(id);

        verify(repository)
                .deleteById(id);

        verifyNoInteractions(outbox);

        verifyNoMoreInteractions(repository);
    }


    @Test
    void shouldReturnFailureWhenOutboxFails() {

        UrlRedirectRuleModel rule = createRule();


        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        when(repository.deleteById(id))
                .thenReturn(1);


        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(
                        Result.failure(
                                500,
                                "Failed to create outbox event"
                        )
                );


        Result<Void> result = service.execute(id);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(500);

        assertThat(result.getMessage())
                .isEqualTo("Failed to create outbox event");


        verify(repository)
                .findById(id);

        verify(repository)
                .deleteById(id);

        verify(outbox)
                .execute(any(CreateOutboxEventCommand.class));


        verifyNoMoreInteractions(repository, outbox);
    }


    @Test
    void shouldPropagateUnexpectedException() {

        when(repository.findById(id))
                .thenThrow(new RuntimeException("database error"));


        try {

            service.execute(id);

        } catch (Exception e) {

            assertThat(e.getMessage())
                    .isEqualTo("database error");
        }


        verify(repository)
                .findById(id);

        verifyNoInteractions(outbox);
    }
}