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
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindByKeyApiKeyServiceTest {

    @Mock
    private IApiKeyRepository repository;

    @InjectMocks
    private FindByKeyApiKeyService service;

    private ApiKeyModel apiKey;

    private String rawKey;

    @BeforeEach
    void setup() {
        rawKey = "wk_live_123456789";

        apiKey = new ApiKeyModel();
        apiKey.setId(1L);
        apiKey.setName("integration-key");
        apiKey.setActive(true);
    }

    @Test
    void shouldReturnApiKeyWhenFound() {

        String expectedHash = sha256(rawKey);

        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.of(apiKey));

        Result<ApiKeyModel> result =
                service.execute(rawKey);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();

        assertThat(result.getStatusCode())
                .isEqualTo(200);

        assertThat(result.getValue())
                .isSameAs(apiKey);

        verify(repository, times(1))
                .findByKeyHash(expectedHash);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenApiKeyDoesNotExist() {

        String expectedHash = sha256(rawKey);

        when(repository.findByKeyHash(expectedHash))
                .thenReturn(Optional.empty());

        Result<ApiKeyModel> result =
                service.execute(rawKey);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.isSuccess()).isFalse();

        assertThat(result.getStatusCode())
                .isEqualTo(404);

        assertThat(result.getErrors())
                .containsExactly("Api key not found");

        assertThat(result.getValue())
                .isNull();

        verify(repository, times(1))
                .findByKeyHash(expectedHash);

        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldGenerateCorrectSha256Hash() {

        when(repository.findByKeyHash(anyString()))
                .thenReturn(Optional.empty());

        service.execute(rawKey);

        ArgumentCaptor<String> hashCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(repository)
                .findByKeyHash(hashCaptor.capture());

        String hashSentToRepository =
                hashCaptor.getValue();

        assertThat(hashSentToRepository)
                .isEqualTo(sha256(rawKey));
    }

    @Test
    void shouldCallRepositoryOnlyOnce() {

        when(repository.findByKeyHash(anyString()))
                .thenReturn(Optional.empty());

        service.execute(rawKey);

        verify(repository, only())
                .findByKeyHash(anyString());
    }

    @Test
    void shouldGenerateSameHashForSameKey() {

        when(repository.findByKeyHash(anyString()))
                .thenReturn(Optional.empty());

        service.execute(rawKey);
        service.execute(rawKey);

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);

        verify(repository, times(2))
                .findByKeyHash(captor.capture());

        assertThat(captor.getAllValues())
                .hasSize(2)
                .allMatch(hash -> hash.equals(sha256(rawKey)));
    }

    private String sha256(String value) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}