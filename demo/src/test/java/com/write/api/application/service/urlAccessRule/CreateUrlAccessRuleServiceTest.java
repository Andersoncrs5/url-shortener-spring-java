package com.write.api.application.service.urlAccessRule;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.mapper.urlAccessRule.CreateUrlAccessRuleMapper;
import com.write.api.application.service.base.BaseServiceTest;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateUrlAccessRuleServiceTest extends BaseServiceTest {

    @Mock
    private IUrlRepository urlRepository;

    @Mock
    private IUrlAccessRuleRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @Mock
    private CreateUrlAccessRuleMapper mapper;

    @InjectMocks
    private CreateUrlAccessRuleService service;

    private CreateUrlAccessRuleDTO dto;
    private UrlAccessRuleModel mappedModel;

    private final Long assignedByUserId = 50L;
    private final Long generatedId = 999L;

    @BeforeEach
    void setup() {
        dto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.COUNTRY_BLOCK,
                "BR",
                LocalDateTime.now().plusDays(1)
        );

        mappedModel = new UrlAccessRuleModel();
        mappedModel.setUrlId(dto.urlId());
        mappedModel.setType(dto.type());
        mappedModel.setRuleValue(dto.ruleValue());
        mappedModel.setExpiresAt(dto.expiresAt());
    }

    @Test
    void shouldCreateAccessRuleSuccessfully() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(10);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(outbox.execute(any(CreateOutboxEventCommand.class))).thenReturn(Result.success());

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenAnswer(invocation -> {
                    UrlAccessRuleModel arg = invocation.getArgument(0);
                    arg.setCreatedAt(LocalDateTime.now());
                    arg.setUpdatedAt(LocalDateTime.now());
                    return arg;
                });

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue().getId()).isEqualTo(generatedId);

        // VERIFY IN ORDER (Fluxo de execução exato)
        ArgumentCaptor<UrlAccessRuleModel> captor = ArgumentCaptor.forClass(UrlAccessRuleModel.class);

        InOrder order = inOrder(repository, urlRepository, mapper, idGen, outbox);
        order.verify(repository).countByUrlId(dto.urlId());
        order.verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        order.verify(mapper).toDomain(dto);
        order.verify(idGen).nextId();
        order.verify(repository).insert(captor.capture());
        order.verify(outbox).execute(any(CreateOutboxEventCommand.class));

        UrlAccessRuleModel captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(generatedId);
        assertThat(captured.getAssignedByUserId()).isEqualTo(assignedByUserId);

        verifyNoMoreInteractions(repository, urlRepository, mapper, idGen, outbox);
    }

    @Test
    void shouldReturnBadRequestWhenMaxRulesLimitReached() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(50); // Limite atingindo

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Number max of rule is 50");

        verify(repository).countByUrlId(dto.urlId());
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(urlRepository, mapper, idGen, outbox);
    }

    @Test
    void shouldReturnFailureWhenOutboxPublishingFails() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(0);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);
        when(repository.insert(any(UrlAccessRuleModel.class))).thenReturn(mappedModel);

        // Simula falha interna no evento de outbox
        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.failure(List.of("Outbox table locked"), 500));

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getErrors()).contains("Outbox table locked");

        InOrder order = inOrder(repository, urlRepository, mapper, idGen, outbox);
        order.verify(repository).countByUrlId(dto.urlId());
        order.verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        order.verify(repository).insert(any(UrlAccessRuleModel.class));
        order.verify(outbox).execute(any(CreateOutboxEventCommand.class));
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotOwnUrl() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(5);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(false);

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getMessage()).isEqualTo("You do not have permission to manage this URL");

        InOrder order = inOrder(repository, urlRepository);
        order.verify(repository).countByUrlId(dto.urlId());
        order.verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());

        verifyNoInteractions(mapper, idGen, outbox);
    }

    @Test
    void shouldReturnConflictWhenRuleAlreadyExists() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(0);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException("dup", new RuntimeException("uk_url_access_rule")));

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).isEqualTo("Access rule already present for this url");
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldReturnNotFoundWhenUrlDoesNotExist() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(0);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException("fk", new RuntimeException("fk_url_access_rule_url_id")));

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url not found");
    }

    @Test
    void shouldReturnNotFoundWhenAssignedUserDoesNotExist() {
        // GIVEN
        when(repository.countByUrlId(dto.urlId())).thenReturn(0);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException("fk", new RuntimeException("fk_url_access_rule_user_id")));

        // WHEN
        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        // THEN
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Assigned user not found");
    }

    @Test
    void shouldReturnBadRequestWhenMaxClicksIsNotPositive() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.MAX_CLICKS, "0", LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("MAX_CLICKS must be greater than zero");
    }

    @Test
    void shouldReturnBadRequestWhenRateLimitIsNotANumber() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.RATE_LIMIT, "abc", LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("RATE_LIMIT must be a valid number");
    }

    @Test
    void shouldReturnBadRequestWhenRateLimitIsNotPositive() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.RATE_LIMIT, "-5", LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("RATE_LIMIT must be greater than zero");
    }

    @Test
    void shouldReturnBadRequestWhenExpiresAtIsNullForExpiresAtRule() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.EXPIRES_AT, "val", null
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("EXPIRES_AT requires an expiration date");
    }

    @Test
    void shouldReturnBadRequestWhenExpiresAtIsPast() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.EXPIRES_AT, "val", LocalDateTime.now().minusMinutes(5)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("EXPIRES_AT must be a future date");
    }

    @Test
    void shouldReturnBadRequestWhenCountryCodeIsInvalid() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.COUNTRY_ALLOW, "USA", LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).contains("ISO-3166 alpha-2 code");
    }

    @Test
    void shouldReturnBadRequestWhenIpIsInvalid() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.IP_ALLOW, "192.168.1.300", LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Invalid IP address");
    }

    @Test
    void shouldReturnBadRequestWhenUserAgentIsBlank() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L, UrlAccessRuleTypeEnum.USER_AGENT_BLOCK, "   ", LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("User agent cannot be empty");
    }

    @Test
    void shouldThrowInternalServerErrorForUnexpectedException() {
        when(repository.countByUrlId(dto.urlId())).thenReturn(0);
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId())).thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new RuntimeException("database dead"));

        assertThatThrownBy(() -> service.execute(dto, assignedByUserId))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("database dead");
    }
}