package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.InsertDeadLetterEventUseCase;
import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.kafka.core.KafkaTemplate;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveDeadLetterEventUseCaseImpl implements InsertDeadLetterEventUseCase {
    DeadLetterEventRepository repository;

    @Override
    public @NotNull Result<DeadLetterEventModel> execute(DeadLetterEventModel letter) {
        if (!repository.existsById(letter.getId())) {
            return Result.failure(
                    "Dead letter event not found",
                    404
            );
        }

        DeadLetterEventModel saved = repository.save(letter);


        return Result.success(saved, 201);
    }
}
