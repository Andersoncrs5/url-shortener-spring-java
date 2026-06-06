package com.write.api.application.service.apiKey;

import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.mapper.apiKey.CreateApiKeyMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.in.apiKey.CreateApiKeyUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateApiKeyService implements CreateApiKeyUseCase {

    SnowflakeIdGenerator idGen;
    IApiKeyRepository repository;
    IUserRoleRepository userRoleRepository;
    CreateApiKeyMapper mapper;

    @Override
    @ResultTransaction
    @TrackExecutionTime("apikey.create")
    public Result<String> execute(
            CreateApiKeyDTO dto,
            Long userId
    ) {
        if (dto.expiresAt() != null && dto.expiresAt().isBefore(LocalDateTime.now())) {
            return Result.failure(
                    "Expiration date must be in the future",
                    400
            );
        }

        List<String> role = userRoleRepository.findRoleByUserId(userId);

        boolean isAdmin = role.contains("ADMIN") || role.contains("SUPER_ADMIN");

        if (!isAdmin) {
            return Result.failure(
                    "Only ADMIN or SUPER_ADMIN can perform this action",
                    403
            );
        }

        String key =
                UUID.randomUUID().toString().replace("-", "")
                        + UUID.randomUUID().toString().replace("-", "");

        String hash = this.sha256(key);

        ApiKeyModel apiKey = mapper.toDomain(dto);

        apiKey.setId(idGen.nextId());
        apiKey.setUserId(userId);
        apiKey.setKeyHash(hash);
        apiKey.setOwnerUserId(dto.ownerUserId());

        try {

            repository.insert(apiKey);

            return Result.success(key, 201);

        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return Result.failure(
                        "Database constraint violation",
                        409
                );
            }

            String normalized = message.toLowerCase();

            if (normalized.contains("uk_api_keys_name")) {
                return Result.failure(
                        "An API key with this name already exists",
                        409
                );
            }

            if (normalized.contains("uk_api_keys_key_hash")) {
                return Result.failure(
                        "Generated API key already exists",
                        409
                );
            }

            if (normalized.contains("fk_api_keys_user_id")) {
                return Result.failure(
                        "User not found",
                        404
                );
            }

            if (normalized.contains("uk_api_keys_owner_name")) {
                return Result.failure(
                        "Owner User not found",
                        404
                );
            }

            return Result.failure(
                    "Database constraint violation",
                    409
            );

        } catch (Exception e) {

            log.error(
                    "Error creating api key for user {}",
                    userId,
                    e
            );

            throw new InternalServerErrorException(
                    "Error creating api key"
            );
        }
    }

    public String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}