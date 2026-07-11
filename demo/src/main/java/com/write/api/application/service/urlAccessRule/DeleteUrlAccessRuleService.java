package com.write.api.application.service.urlAccessRule;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.urlAccessRule.UrlAccessRuleCreatedEvent;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.application.shared.annotations.UseService;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.urlAccessRule.DeleteUrlAccessRuleUseCase;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlAccessRuleService implements DeleteUrlAccessRuleUseCase {

    IUrlAccessRuleRepository repository;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.access.delete")
    public Result<Void> execute(Long id) {
        UrlAccessRuleModel rule = this.repository.findById(id).orElse(null);

        if (rule == null) {
            return Result.failure(404, "Url Access Rule not found");
        }

        int deleted = repository.deleteById(id);

        if (deleted == 0) return Result.failure(404, "Url Access Rule not found");

        var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                        AggregateTypeEnum.URL_ACCESS_RULE,
                        rule.getId(),
                        EventTypeEnum.URL_ACCESS_RULE_DELETED,
                        TopicEnum.URL_ACCESS_RULE_DELETED,
                        UrlAccessRuleCreatedEvent.create(
                                rule.getId(),
                                rule.getUrlId(),
                                rule.getAssignedByUserId(),
                                rule.getRuleValue(),
                                rule.getType(),
                                rule.getCreatedAt()
                        )
                )
        );

        if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

        return Result.success();
    }
}
