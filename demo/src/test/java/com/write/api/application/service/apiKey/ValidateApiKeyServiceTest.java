package com.write.api.application.service.apiKey;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.out.repository.IApiKeyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateApiKeyServiceTest {

    @Mock
    private IApiKeyRepository repository;

    @InjectMocks
    private ValidateApiKeyService service;

    private String apiKey;
    private String expectedHash;
    private ApiKeyModel key;

    @BeforeEach
    void setup() {
        apiKey = "wk_live_123456789";
        expectedHash = sha256(apiKey);

        key = new ApiKeyModel();
        key.setId(1L);
        key.setActive(true);
        key.setExpiresAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void shouldReturnTrueWhenApiKeyIsValid() {
        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.of(key));

        Result<Boolean> result = service.execute(apiKey);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isTrue();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(repository).findByKeyHash(captor.capture());

        assertThat(captor.getValue()).isEqualTo(expectedHash);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn403WhenApiKeyIsInvalid() {
        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.empty());

        Result<Boolean> result = service.execute(apiKey);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getErrors().getFirst()).isEqualTo("Invalid API key");
        assertThat(result.getValue()).isFalse();

        verify(repository).findByKeyHash(expectedHash);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn403WhenApiKeyIsInactive() {
        key.setActive(false);

        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.of(key));

        Result<Boolean> result = service.execute(apiKey);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getErrors().getFirst()).isEqualTo("API key is inactive");
        assertThat(result.getValue()).isFalse();

        verify(repository).findByKeyHash(expectedHash);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn403WhenApiKeyIsExpired() {
        key.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.of(key));

        Result<Boolean> result = service.execute(apiKey);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(403);
        assertThat(result.getErrors().getFirst()).isEqualTo("API key has expired");
        assertThat(result.getValue()).isFalse();

        verify(repository).findByKeyHash(expectedHash);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldAllowApiKeyWithoutExpirationDate() {
        key.setExpiresAt(null);

        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.of(key));

        Result<Boolean> result = service.execute(apiKey);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isTrue();

        verify(repository).findByKeyHash(expectedHash);
        verifyNoMoreInteractions(repository);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}