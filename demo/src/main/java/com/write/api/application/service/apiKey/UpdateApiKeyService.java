package com.write.api.application.service.apiKey;

import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.application.mapper.apiKey.UpdateApiKeyMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.ports.in.apiKey.UpdateApiKeyUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateApiKeyService implements UpdateApiKeyUseCase {

    IApiKeyRepository repository;
    IUserRoleRepository userRoleRepository;
    UpdateApiKeyMapper mapper;

    @Override
    @ResultTransaction
    @TrackExecutionTime("apikey.update")
    public Result<ApiKeyModel> execute(
            UpdateApiKeyDTO dto,
            Long id,
            Long userId
    ) {

        List<String> role = userRoleRepository.findRoleByUserId(userId);

        boolean isAdmin = role.contains("ADMIN") || role.contains("SUPER_ADMIN");

        if (!isAdmin) {
            return Result.failure(
                    "Only ADMIN or SUPER_ADMIN can perform this action",
                    403
            );
        }

        if (dto.expiresAt() != null && dto.expiresAt().isBefore(LocalDateTime.now())) {
            return Result.failure(
                    "Expiration date must be in the future",
                    400
            );
        }

        ApiKeyModel apiKey = repository.findById(id).orElse(null);

        if (apiKey == null) {
            return Result.failure(
                    "Api key not found",
                    404
            );
        }

        mapper.update(dto, apiKey);

        ApiKeyModel saved = null;
        try {
            saved = repository.save(apiKey);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            String normalized = message.toLowerCase();

            if (normalized.contains("uk_api_keys_name")) {
                return Result.failure(
                        "An API key with this name already exists",
                        409
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {

            log.error(
                    "Error updating api key {}",
                    id,
                    e
            );

            throw new InternalServerErrorException(
                    "Error updating api key"
            );
        }

        log.info("Api key updated with success");
        return Result.success(saved, 200);
    }
}