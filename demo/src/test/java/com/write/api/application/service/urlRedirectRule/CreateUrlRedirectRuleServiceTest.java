package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.urlRedirectRule.UrlRedirectRuleCreatedEvent;
import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.mapper.urlRedirectRule.CreateUrlRedirectRuleServiceMapper;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateUrlRedirectRuleServiceTest extends BaseServiceTest {

    @Mock
    private CreateUrlRedirectRuleServiceMapper mapper;

    @Mock
    private IUrlRedirectRuleRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @InjectMocks
    private CreateUrlRedirectRuleService service;

    private CreateUrlRedirectRuleDTO dto;
    private UrlRedirectRuleModel mapped;

    private final Long urlId = 100L;
    private final Long id = 7563458973674679L;

    @BeforeEach
    void setup() {
        dto = new CreateUrlRedirectRuleDTO(
                urlId,
                "BR",
                "Nordeste",
                null,
                null,
                null,
                null,
                "https://google.com",
                1,
                true,
                null,
                null
        );

        mapped = new UrlRedirectRuleModel();
        mapped.setUrlId(urlId);
        mapped.setCountryCode("BR");
        mapped.setRegion("Nordeste");
        mapped.setRedirectUrl("https://google.com");
        mapped.setPriority(1);
        mapped.setActive(true);
    }

    @Test
    void shouldCreateUrlRedirectRuleSuccessfully() {

        when(repository.countByUrlId(urlId))
                .thenReturn(1);

        when(mapper.toModel(dto))
                .thenReturn(mapped);

        when(idGen.nextId())
                .thenReturn(id);

        when(repository.insert(any()))
                .thenAnswer(invocation -> {
                    UrlRedirectRuleModel model =
                            invocation.getArgument(0);

                    model.setCreatedAt(LocalDateTime.now());

                    return model;
                });

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success());


        Result<UrlRedirectRuleModel> result =
                service.execute(dto);


        assertThat(result.isSuccess())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(201);

        assertThat(result.getValue().getId())
                .isEqualTo(id);


        ArgumentCaptor<CreateOutboxEventCommand> captor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);


        verify(outbox)
                .execute(captor.capture());


        CreateOutboxEventCommand command =
                captor.getValue();


        assertThat(command.aggregateType())
                .isEqualTo(AggregateTypeEnum.URL_REDIRECT_RULE);

        assertThat(command.aggregateId())
                .isEqualTo(id);

        assertThat(command.eventType())
                .isEqualTo(EventTypeEnum.URL_REDIRECT_RULE_CREATED);

        assertThat(command.topic())
                .isEqualTo(TopicEnum.URL_REDIRECT_RULE_CREATED);


        UrlRedirectRuleCreatedEvent event =
                (UrlRedirectRuleCreatedEvent) command.payload();


        assertThat(event.id())
                .isEqualTo(id);

        assertThat(event.urlId())
                .isEqualTo(urlId);


        verify(repository)
                .insert(any());

        verifyNoMoreInteractions(
                repository,
                mapper,
                outbox
        );
    }


    @Test
    void shouldReturn400WhenLimitRulesExceeded() {

        when(repository.countByUrlId(urlId))
                .thenReturn(50);


        Result<UrlRedirectRuleModel> result =
                service.execute(dto);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(400);

        assertThat(result.getMessage())
                .isEqualTo("Number max of rule is 50");


        verify(repository, never())
                .insert(any());

        verifyNoInteractions(outbox);
    }


    @Test
    void shouldReturn409WhenRuleAlreadyExists() {

        when(repository.countByUrlId(urlId))
                .thenReturn(1);

        when(mapper.toModel(dto))
                .thenReturn(mapped);


        when(repository.insert(any()))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate",
                                new RuntimeException(
                                        "uk_url_redirect_rules_hash"
                                )
                        )
                );


        Result<UrlRedirectRuleModel> result =
                service.execute(dto);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(409);

        assertThat(result.getMessage())
                .contains("Rule already present");
    }


    @Test
    void shouldReturn404WhenUrlDoesNotExist() {

        when(repository.countByUrlId(urlId))
                .thenReturn(1);

        when(mapper.toModel(dto))
                .thenReturn(mapped);


        when(repository.insert(any()))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "fk",
                                new RuntimeException(
                                        "fk_url_redirect_rules_url"
                                )
                        )
                );


        Result<UrlRedirectRuleModel> result =
                service.execute(dto);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(404);

        assertThat(result.getMessage())
                .contains("Url not found");
    }


    @Test
    void shouldThrowInternalServerError() {

        when(repository.countByUrlId(urlId))
                .thenReturn(1);

        when(mapper.toModel(dto))
                .thenReturn(mapped);


        when(repository.insert(any()))
                .thenThrow(
                        new RuntimeException("boom")
                );


        assertThatThrownBy(() -> service.execute(dto))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("boom");
    }


    @Test
    void shouldFailWhenOutboxFails() {

        when(repository.countByUrlId(urlId))
                .thenReturn(1);

        when(mapper.toModel(dto))
                .thenReturn(mapped);

        when(idGen.nextId())
                .thenReturn(id);

        when(repository.insert(any()))
                .thenReturn(mapped);


        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(
                        Result.failure(
                                500,
                                "Outbox error"
                        )
                );


        Result<UrlRedirectRuleModel> result =
                service.execute(dto);


        assertThat(result.isFailure())
                .isTrue();

        assertThat(result.getStatusCode())
                .isEqualTo(500);

        assertThat(result.getMessage())
                .isEqualTo("Outbox error");


        verify(repository)
                .insert(any());

        verify(outbox)
                .execute(any(CreateOutboxEventCommand.class));
    }
}