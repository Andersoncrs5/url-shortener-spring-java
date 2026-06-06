package com.write.api.application.service.apiKey;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.in.apiKey.FindByKeyApiKeyUseCase;
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
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindByKeyApiKeyService implements FindByKeyApiKeyUseCase {

    IApiKeyRepository repository;

    @TrackExecutionTime("apikey.find")
    public Result<ApiKeyModel> execute(String key) {
        String hash = sha256(key);

        Optional<ApiKeyModel> optional =
                repository.findByKeyHash(hash);

        if (optional.isEmpty()) {
            return Result.failure(404, "Api key not found");
        }

        ApiKeyModel model = optional.get();

        return Result.success(model, 200);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            value.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
