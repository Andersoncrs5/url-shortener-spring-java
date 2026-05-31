package com.write.api.application.service.urlAccessRuleService;

import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.mapper.urlAccessRule.CreateUrlAccessRuleMapper;
import com.write.api.application.service.urlAccessRule.CreateUrlAccessRuleService;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUrlAccessRuleServiceTest {

    @Mock
    private SnowflakeIdGenerator idGen;

    @Mock
    private IUrlRepository urlRepository;

    @Mock
    private IUrlAccessRuleRepository repository;

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
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId()))
                .thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenAnswer(invocation -> {
                    UrlAccessRuleModel arg = invocation.getArgument(0);
                    arg.setCreatedAt(LocalDateTime.now());
                    arg.setUpdatedAt(LocalDateTime.now());
                    return arg;
                });

        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();

        UrlAccessRuleModel value = result.getValue();
        assertThat(value.getId()).isEqualTo(generatedId);
        assertThat(value.getUrlId()).isEqualTo(dto.urlId());
        assertThat(value.getType()).isEqualTo(dto.type());
        assertThat(value.getRuleValue()).isEqualTo(dto.ruleValue());
        assertThat(value.getAssignedByUserId()).isEqualTo(assignedByUserId);
        assertThat(value.getExpiresAt()).isEqualTo(dto.expiresAt());
        assertThat(value.getCreatedAt()).isNotNull();
        assertThat(value.getUpdatedAt()).isNotNull();

        ArgumentCaptor<UrlAccessRuleModel> captor =
                ArgumentCaptor.forClass(UrlAccessRuleModel.class);

        InOrder order = inOrder(urlRepository, mapper, idGen, repository);
        order.verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        order.verify(mapper).toDomain(dto);
        order.verify(idGen).nextId();
        order.verify(repository).insert(captor.capture());

        UrlAccessRuleModel captured = captor.getValue();
        assertThat(captured.getId()).isEqualTo(generatedId);
        assertThat(captured.getAssignedByUserId()).isEqualTo(assignedByUserId);
        assertThat(captured.getUrlId()).isEqualTo(dto.urlId());
        assertThat(captured.getType()).isEqualTo(dto.type());
        assertThat(captured.getRuleValue()).isEqualTo(dto.ruleValue());
        assertThat(captured.getExpiresAt()).isEqualTo(dto.expiresAt());

        verifyNoMoreInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnForbiddenWhenUserDoesNotOwnUrl() {
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId()))
                .thenReturn(false);

        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getMessage())
                .isEqualTo("You do not have permission to manage this URL");
        assertThat(result.getValue()).isNull();

        verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        verifyNoInteractions(mapper);
        verifyNoInteractions(idGen);
        verifyNoInteractions(repository);
        verifyNoMoreInteractions(urlRepository);
    }

    @Test
    void shouldReturnConflictWhenRuleAlreadyExists() {
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId()))
                .thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_access_rule")
                ));

        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Access rule already present for this url");

        verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(UrlAccessRuleModel.class));
        verifyNoMoreInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnNotFoundWhenUrlDoesNotExist() {
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId()))
                .thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk",
                        new RuntimeException("fk_url_access_rule_url_id")
                ));

        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url not found");

        verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(UrlAccessRuleModel.class));
        verifyNoMoreInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnNotFoundWhenAssignedUserDoesNotExist() {
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId()))
                .thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk",
                        new RuntimeException("fk_url_access_rule_user_id")
                ));

        Result<UrlAccessRuleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Assigned user not found");

        verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(UrlAccessRuleModel.class));
        verifyNoMoreInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenMaxClicksIsNotANumber() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.MAX_CLICKS,
                "abc",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("MAX_CLICKS must be a valid number");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenMaxClicksIsNotPositive() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.MAX_CLICKS,
                "0",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("MAX_CLICKS must be greater than zero");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenRateLimitIsNotANumber() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.RATE_LIMIT,
                "abc",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("RATE_LIMIT must be a valid number");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenRateLimitIsNotPositive() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.RATE_LIMIT,
                "-1",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("RATE_LIMIT must be greater than zero");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenExpiresAtIsNullForExpiresAtRule() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.EXPIRES_AT,
                "anything",
                null
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("EXPIRES_AT requires an expiration date");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenExpiresAtIsPast() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.EXPIRES_AT,
                "anything",
                LocalDateTime.now().minusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("EXPIRES_AT must be a future date");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenCountryCodeIsInvalid() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.COUNTRY_BLOCK,
                "BRA",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("Country code must be a valid ISO-3166 alpha-2 code (e.g. BR, US, FR)");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenIpIsInvalid() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.IP_BLOCK,
                "999.999.999.999",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("Invalid IP address");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldReturnBadRequestWhenUserAgentIsBlank() {
        CreateUrlAccessRuleDTO invalidDto = new CreateUrlAccessRuleDTO(
                1L,
                UrlAccessRuleTypeEnum.USER_AGENT_BLOCK,
                "   ",
                LocalDateTime.now().plusDays(1)
        );

        Result<UrlAccessRuleModel> result = service.execute(invalidDto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("User agent cannot be empty");

        verifyNoInteractions(urlRepository, mapper, idGen, repository);
    }

    @Test
    void shouldThrowInternalServerErrorForUnexpectedException() {
        when(urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId()))
                .thenReturn(true);
        when(mapper.toDomain(dto)).thenReturn(mappedModel);
        when(idGen.nextId()).thenReturn(generatedId);

        when(repository.insert(any(UrlAccessRuleModel.class)))
                .thenThrow(new RuntimeException("unexpected error"));

        assertThatThrownBy(() -> service.execute(dto, assignedByUserId))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected error");

        verify(urlRepository).existsByUserIdAndUrlId(assignedByUserId, dto.urlId());
        verify(mapper).toDomain(dto);
        verify(idGen).nextId();
        verify(repository).insert(any(UrlAccessRuleModel.class));
        verifyNoMoreInteractions(urlRepository, mapper, idGen, repository);
    }
}
