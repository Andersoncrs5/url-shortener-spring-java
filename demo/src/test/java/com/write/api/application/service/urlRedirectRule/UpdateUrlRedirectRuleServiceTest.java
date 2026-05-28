package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.mapper.urlRedirectRule.UpdateUrlRedirectRuleServiceMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUrlRedirectRuleServiceTest {

    @Mock
    private IUrlRedirectRuleRepository repository;

    @Mock
    private UpdateUrlRedirectRuleServiceMapper mapper;

    @InjectMocks
    private UpdateUrlRedirectRuleService service;

    private UrlRedirectRuleModel rule;
    private UpdateUrlRedirectRuleDTO dto;

    private final Long id = 1L;

    @BeforeEach
    void setup() {
        rule = new UrlRedirectRuleModel();

        rule.setId(id);
        rule.setUrlId(10L);
        rule.setCountryCode("BR");
        rule.setRegion("PI");
        rule.setMatchType(MatchTypeEnum.EXACT);
        rule.setRedirectUrl("https://old-url.com");
        rule.setPriority(1);
        rule.setActive(true);
        rule.setCreatedAt(LocalDateTime.now().minusDays(1));
        rule.setUpdatedAt(LocalDateTime.now().minusHours(1));

        dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "CA",
                null,
                null,
                null,
                MatchTypeEnum.PARTIAL,
                "https://new-url.com",
                5,
                false,
                null,
                null
        );
    }

    @Test
    void shouldUpdateUrlRedirectRuleSuccessfully() {
        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        doAnswer(invocation -> {
            UpdateUrlRedirectRuleDTO source = invocation.getArgument(0);
            UrlRedirectRuleModel target = invocation.getArgument(1);

            target.setCountryCode(source.countryCode());
            target.setRegion(source.region());
            target.setMatchType(source.matchType());
            target.setRedirectUrl(source.redirectUrl());
            target.setPriority(source.priority());
            target.setActive(source.active());

            return null;
        }).when(mapper).update(eq(dto), any(UrlRedirectRuleModel.class));

        when(repository.save(any(UrlRedirectRuleModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<UrlRedirectRuleModel> result =
                service.execute(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();

        UrlRedirectRuleModel saved = result.getValue();

        assertThat(saved.getCountryCode()).isEqualTo("US");
        assertThat(saved.getRegion()).isEqualTo("CA");
        assertThat(saved.getMatchType()).isEqualTo(MatchTypeEnum.PARTIAL);
        assertThat(saved.getRedirectUrl()).isEqualTo("https://new-url.com");
        assertThat(saved.getPriority()).isEqualTo(5);
        assertThat(saved.isActive()).isFalse();
        assertThat(saved.getRuleHash()).isNotNull();

        ArgumentCaptor<UrlRedirectRuleModel> captor =
                ArgumentCaptor.forClass(UrlRedirectRuleModel.class);

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(captor.capture());

        UrlRedirectRuleModel savedArg = captor.getValue();

        assertThat(savedArg.getCountryCode()).isEqualTo("US");
        assertThat(savedArg.getRegion()).isEqualTo("CA");
        assertThat(savedArg.getMatchType()).isEqualTo(MatchTypeEnum.PARTIAL);
        assertThat(savedArg.getRedirectUrl()).isEqualTo("https://new-url.com");
        assertThat(savedArg.getPriority()).isEqualTo(5);
        assertThat(savedArg.isActive()).isFalse();
        assertThat(savedArg.getRuleHash()).isNotBlank();

        InOrder order = inOrder(repository, mapper);

        order.verify(repository).findById(id);
        order.verify(mapper).update(dto, rule);
        order.verify(repository).save(rule);

        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldReturn404WhenRuleNotFound() {
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Result<UrlRedirectRuleModel> result =
                service.execute(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Rule not found");

        verify(repository).findById(id);

        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturn409WhenRuleAlreadyExists() {
        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        doNothing().when(mapper)
                .update(eq(dto), any(UrlRedirectRuleModel.class));

        when(repository.save(any(UrlRedirectRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_redirect_rules_hash")
                ));

        Result<UrlRedirectRuleModel> result =
                service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Rule already present in url");

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(rule);
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        doNothing().when(mapper)
                .update(eq(dto), any(UrlRedirectRuleModel.class));

        RuntimeException root = mock(RuntimeException.class);

        when(root.getMessage()).thenReturn(null);

        when(repository.save(any(UrlRedirectRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "integrity",
                        root
                ));

        Result<UrlRedirectRuleModel> result =
                service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("Database integrity error");

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(rule);
    }

    @Test
    void shouldReturn400WhenDataTooLongOccurs() {
        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        doNothing().when(mapper)
                .update(eq(dto), any(UrlRedirectRuleModel.class));

        when(repository.save(any(UrlRedirectRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "too long",
                        new RuntimeException(
                                "Data too long for column 'redirect_url'"
                        )
                ));

        Result<UrlRedirectRuleModel> result =
                service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("exceeded the allowed size");

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(rule);
    }

    @Test
    void shouldReturn400WhenRequiredFieldIsNull() {
        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        doNothing().when(mapper)
                .update(eq(dto), any(UrlRedirectRuleModel.class));

        when(repository.save(any(UrlRedirectRuleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "null",
                        new RuntimeException(
                                "Column 'redirect_url' cannot be null"
                        )
                ));

        Result<UrlRedirectRuleModel> result =
                service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("Required field");

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(rule);
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        doNothing().when(mapper)
                .update(eq(dto), any(UrlRedirectRuleModel.class));

        when(repository.save(any(UrlRedirectRuleModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() ->
                service.execute(id, dto)
        )
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(rule);
    }
}