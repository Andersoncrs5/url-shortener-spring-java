package com.write.api.application.service.apiKey;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.in.apiKey.ValidateApiKeyUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidateApiKeyService implements ValidateApiKeyUseCase {

    IApiKeyRepository repository;

    @Override
    @ResultTransaction
    @TrackExecutionTime("apikey.validation.api.key")
    public Result<Boolean> execute(String apiKey) {
        String hash = sha256(apiKey);

        ApiKeyModel key = repository.findByKeyHash(hash).orElse(null);

        if (key == null) {
            return Result.failure(
                    "Invalid API key",
                    403,
                    false
            );
        }

        if (!key.isActive()) {
            return Result.failure(
                    "API key is inactive",
                    403,
                    false
            );
        }

        if (key.getExpiresAt() != null && key.getExpiresAt().isBefore(LocalDateTime.now())) {
            return Result.failure(
                    "API key has expired",
                    403,
                    false
            );
        }

        return Result.success(true);
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