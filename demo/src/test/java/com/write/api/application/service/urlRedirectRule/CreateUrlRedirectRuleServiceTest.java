package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.mapper.urlRedirectRule.CreateUrlRedirectRuleServiceMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUrlRedirectRuleServiceTest {

    @Mock
    private SnowflakeIdGenerator idGen;

    @Mock
    private CreateUrlRedirectRuleServiceMapper mapper;

    @Mock
    private IUrlRedirectRuleRepository repository;

    @InjectMocks
    private CreateUrlRedirectRuleService service;

    private CreateUrlRedirectRuleDTO dto;
    private UrlRedirectRuleModel mapped;
    private UrlRedirectRuleModel saved;

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

        saved = new UrlRedirectRuleModel();
        saved.setId(999L);
        saved.setUrlId(urlId);
        saved.setRedirectUrl("https://google.com");
        saved.setPriority(1);
        saved.setActive(true);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateUrlRedirectRuleSuccessfully() {

        when(mapper.toModel(dto)).thenReturn(mapped);
        when(idGen.nextId()).thenReturn(id);

        when(repository.insert(any())).thenAnswer(invocation -> {
            UrlRedirectRuleModel arg = invocation.getArgument(0);
            arg.setId(id);
            arg.setCreatedAt(LocalDateTime.now());
            return arg;
        });

        Result<UrlRedirectRuleModel> result = service.execute(dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(id);
        assertThat(result.getValue().getUrlId()).isEqualTo(urlId);

        ArgumentCaptor<UrlRedirectRuleModel> captor =
                ArgumentCaptor.forClass(UrlRedirectRuleModel.class);

        verify(mapper).toModel(dto);
        verify(repository).insert(captor.capture());

        UrlRedirectRuleModel inserted = captor.getValue();
        assertThat(inserted.getUrlId()).isEqualTo(urlId);
        assertThat(inserted.getRedirectUrl()).isEqualTo("https://google.com");

        InOrder order = inOrder(mapper, repository);
        order.verify(mapper).toModel(dto);
        order.verify(repository).insert(any());

        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn409WhenRuleAlreadyExists() {
        when(mapper.toModel(dto)).thenReturn(mapped);

        when(repository.insert(any()))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_redirect_rules_hash")
                ));

        Result<UrlRedirectRuleModel> result = service.execute(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).contains("Rule already present in url");

        verify(mapper).toModel(dto);
        verify(repository).insert(any());
    }

    @Test
    void shouldReturn404WhenUrlNotFound() {
        when(mapper.toModel(dto)).thenReturn(mapped);

        when(repository.insert(any()))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_redirect_rules_url")
                ));

        Result<UrlRedirectRuleModel> result = service.execute(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).contains("Url not found");

        verify(mapper).toModel(dto);
        verify(repository).insert(any());
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(mapper.toModel(dto)).thenReturn(mapped);

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any()))
                .thenThrow(new DataIntegrityViolationException("error", root));

        Result<UrlRedirectRuleModel> result = service.execute(dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);

        verify(mapper).toModel(dto);
        verify(repository).insert(any());
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(mapper.toModel(dto)).thenReturn(mapped);

        when(repository.insert(any()))
                .thenThrow(new RuntimeException("boom"));

        assertThatThrownBy(() -> service.execute(dto))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("boom");

        verify(mapper).toModel(dto);
        verify(repository).insert(any());
    }
}