package com.write.api.application.service.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.apiKey.FindByKeyApiKeyUseCase;
import com.write.api.ports.in.apiKey.ValidateApiKeyUseCase;
import com.write.api.ports.in.url.CreateUrlUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUrlFromApiKeyServiceTest {

    @Mock
    private CreateUrlUseCase useCase;

    @Mock
    private ValidateApiKeyUseCase validateApiKey;

    @Mock
    private FindByKeyApiKeyUseCase findByKeyApiKey;

    @InjectMocks
    private CreateUrlFromApiKeyService service;

    private String key;
    private CreateUrlDTO dto;
    private ApiKeyModel apiKey;
    private UrlModel url;

    @BeforeEach
    void setup() {
        key = "wk_live_123456789";

        dto = new CreateUrlDTO(
                "https://example.com",
                "Example title",
                "Example description",
                "https://example.com/favicon.ico",
                "example.com",
                UrlAccessTypeEnum.PUBLIC,
                null,
                LocalDateTime.now().plusDays(7)
        );

        apiKey = new ApiKeyModel();
        apiKey.setId(1L);
        apiKey.setOwnerUserId(99L);
        apiKey.setActive(true);
        apiKey.setExpiresAt(LocalDateTime.now().plusDays(1));

        url = new UrlModel();
        url.setId(10L);
        url.setShortCode("abc123");
    }

    @Test
    void shouldCreateUrlSuccessfullyWithApiKey() {
        when(validateApiKey.execute(key))
                .thenReturn(Result.success(true, 200));

        when(findByKeyApiKey.execute(key))
                .thenReturn(Result.success(apiKey, 200));

        when(useCase.execute(dto, apiKey.getOwnerUserId()))
                .thenReturn(Result.success(url, 201));

        Result<UrlModel> result = service.execute(key, dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isSameAs(url);

        InOrder order = inOrder(validateApiKey, findByKeyApiKey, useCase);
        order.verify(validateApiKey).execute(key);
        order.verify(findByKeyApiKey).execute(key);
        order.verify(useCase).execute(dto, apiKey.getOwnerUserId());

        verifyNoMoreInteractions(validateApiKey, findByKeyApiKey, useCase);
    }

    @Test
    void shouldReturnFailureWhenValidateApiKeyFails() {
        when(validateApiKey.execute(key))
                .thenReturn(Result.failure(500, "Validation error"));

        Result<UrlModel> result = service.execute(key, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getErrors()).containsExactly("Validation error");
        assertThat(result.getValue()).isNull();

        verify(validateApiKey).execute(key);
        verifyNoInteractions(findByKeyApiKey, useCase);
        verifyNoMoreInteractions(validateApiKey);
    }

    @Test
    void shouldReturn409WhenApiKeyIsInvalid() {
        when(validateApiKey.execute(key))
                .thenReturn(Result.success(false, 200));

        Result<UrlModel> result = service.execute(key, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors()).containsExactly("Api key is invalid");
        assertThat(result.getValue()).isNull();

        verify(validateApiKey).execute(key);
        verifyNoInteractions(findByKeyApiKey, useCase);
        verifyNoMoreInteractions(validateApiKey);
    }

    @Test
    void shouldReturnFailureWhenFindByKeyFails() {
        when(validateApiKey.execute(key))
                .thenReturn(Result.success(true, 200));

        when(findByKeyApiKey.execute(key))
                .thenReturn(Result.failure(404, "Api key not found"));

        Result<UrlModel> result = service.execute(key, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors()).containsExactly("Api key not found");
        assertThat(result.getValue()).isNull();

        InOrder order = inOrder(validateApiKey, findByKeyApiKey);
        order.verify(validateApiKey).execute(key);
        order.verify(findByKeyApiKey).execute(key);

        verifyNoInteractions(useCase);
        verifyNoMoreInteractions(validateApiKey, findByKeyApiKey);
    }

    @Test
    void shouldReturn409WhenApiKeyIsDisabled() {
        apiKey.setActive(false);

        when(validateApiKey.execute(key))
                .thenReturn(Result.success(true, 200));

        when(findByKeyApiKey.execute(key))
                .thenReturn(Result.success(apiKey, 200));

        Result<UrlModel> result = service.execute(key, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors()).containsExactly("Api key is disabled");
        assertThat(result.getValue()).isNull();

        verify(validateApiKey).execute(key);
        verify(findByKeyApiKey).execute(key);
        verifyNoInteractions(useCase);
        verifyNoMoreInteractions(validateApiKey, findByKeyApiKey);
    }

    @Test
    void shouldReturn409WhenApiKeyIsExpired() {
        apiKey.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(validateApiKey.execute(key))
                .thenReturn(Result.success(true, 200));

        when(findByKeyApiKey.execute(key))
                .thenReturn(Result.success(apiKey, 200));

        Result<UrlModel> result = service.execute(key, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors()).containsExactly("Api key is expired");
        assertThat(result.getValue()).isNull();

        verify(validateApiKey).execute(key);
        verify(findByKeyApiKey).execute(key);
        verifyNoInteractions(useCase);
        verifyNoMoreInteractions(validateApiKey, findByKeyApiKey);
    }
}