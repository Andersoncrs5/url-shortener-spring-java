package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.urlRedirectRule.UrlRedirectRuleDeletedEvent;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.urlRedirectRule.DeleteUrlRedirectRuleUseCase;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlRedirectRuleService implements DeleteUrlRedirectRuleUseCase {
    IUrlRedirectRuleRepository repository;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.redirect.delete")
    public Result<Void> execute(Long id) {
        var rule = this.repository.findById(id).orElse(null);

        if (rule == null) return Result.failure(404, "Url Access Rule not found");

        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url Rule not found");
        }

        var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                        AggregateTypeEnum.URL_REDIRECT_RULE,
                        rule.getId(),
                        EventTypeEnum.URL_REDIRECT_RULE_DELETED,
                        TopicEnum.URL_REDIRECT_RULE_DELETED,
                        UrlRedirectRuleDeletedEvent.create(
                                rule.getId(),
                                rule.getUrlId(),
                                rule.getCreatedAt()
                        )
                )
        );

        if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

        return Result.success();
    }

}
