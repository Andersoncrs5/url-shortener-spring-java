package com.write.api.application.service.url;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.url.UrlDeleteEvent;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.url.DeleteUrlByIdForceUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlByIdForceService implements DeleteUrlByIdForceUseCase {

    IUrlRepository repository;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    public Result<Void> execute(Long id) {
        UrlModel url = this.repository.findById(id).orElse(null);

        if (url == null) {
            return Result.failure(404, "Url not found");
        }

        var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                        AggregateTypeEnum.URL,
                        url.getId(),
                        EventTypeEnum.URL_DELETED,
                        TopicEnum.URL_DELETED,
                        UrlDeleteEvent.create(
                                url.getId(),
                                url.getTitle(),
                                url.getShortCode()
                        )
                )
        );

        if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

        repository.deleteById(id);

        return Result.success();
    }

}
