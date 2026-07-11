package com.write.api.application.service.apiKey;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.apiKey.ApiKeyCreatedEvent;
import com.write.api.application.dto.outbox.events.apiKey.ApiKeyDeletedEvent;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.application.shared.annotations.UseService;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.ports.in.apiKey.DeleteApiKeyUseCase;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteApiKeyService implements DeleteApiKeyUseCase {

    IApiKeyRepository repository;
    IUserRoleRepository userRoleRepository;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    @TrackExecutionTime("apikey.delete")
    public Result<Void> execute(Long id, Long userId) {
        var key = repository.findById(id).orElse(null);

        if (key == null) {
            return Result.failure(404, "Api key not found");
        }

        List<String> role = userRoleRepository.findRoleByUserId(userId);

        boolean isAdmin = role.contains("ADMIN") || role.contains("SUPER_ADMIN");

        if (!isAdmin) {
            return Result.failure(
                    "Only ADMIN or SUPER_ADMIN can perform this action",
                    403
            );
        }

        int deleted = repository.deleteById(id);

        if (deleted == 0) return Result.failure(404, "Api key not found");
        log.info("Api key deleted with success");

        var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                        AggregateTypeEnum.API_KEY,
                        key.getId(),
                        EventTypeEnum.API_KEY_DELETED,
                        TopicEnum.API_KEY_DELETED,
                        ApiKeyDeletedEvent.create(
                                key.getId(),
                                key.getName(),
                                key.getUserId(),
                                key.getOwnerUserId(),
                                key.isActive()
                        )
                )
        );

        if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

        return Result.success();
    }
}
