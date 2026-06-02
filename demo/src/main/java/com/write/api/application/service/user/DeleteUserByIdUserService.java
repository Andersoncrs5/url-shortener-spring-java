package com.write.api.application.service.user;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserDeletedEvent;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.user.DeleteByIdUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUserByIdUserService implements DeleteByIdUserUseCase {

    CreateOutboxEventUseCase outbox;
    IUserRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> deleteById(Long id) {
        UserModel user = this.repository.findById(id).orElse(null);

        if (user == null)
            return Result.failure("User not found", 404);

        var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                        AggregateTypeEnum.USER,
                        id,
                        EventTypeEnum.USER_DELETED,
                        TopicEnum.USER_DELETED,
                        UserDeletedEvent.create(
                                user.getId(),
                                user.getName(),
                                user.getEmail()
                        )
                )
        );

        if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

        int deleted = repository.deleteById(id);

        if (deleted != 1) {
            throw new IllegalStateException(
                    "Expected 1 row deleted but got " + deleted
            );
        }

        return Result.success(200);
    }

}
