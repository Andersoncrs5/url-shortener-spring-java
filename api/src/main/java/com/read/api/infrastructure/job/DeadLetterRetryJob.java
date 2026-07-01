package com.read.api.infrastructure.job;

import com.read.api.application.usecase.interfaces.role.TryRetryRoleUseCase;
import com.read.api.application.usecase.interfaces.url.TryRetryUrlUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.TryRetryUrlAccessRuleUseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.TryRetryUrlRedirectRuleUseCase;
import com.read.api.application.usecase.interfaces.urlTag.TryRetryUrlTagUseCase;
import com.read.api.application.usecase.interfaces.user.TryRetryUserUseCase;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeadLetterRetryJob {

    DeadLetterEventRepository repository;

    TryRetryUserUseCase retryUser;
    TryRetryRoleUseCase retryRole;
    TryRetryUrlUseCase retryUrl;
    TryRetryUrlAccessRuleUseCase retryUrlAccess;
    TryRetryUrlRedirectRuleUseCase retryUrlRedirect;
    TryRetryUrlTagUseCase retryUrlTag;

    @Scheduled(fixedDelay = 30000)
    public void execute() {

        List<DeadLetterEventModel> events = repository.findPendingRetryEvents(
                LocalDateTime.now(),
                PageRequest.of(0, 100)
        );

        for (DeadLetterEventModel event : events) {
            try {
                if (event.getEventType().equalsIgnoreCase("roles")) {
                    Result<Void> executed = retryRole.execute(event);

                    if (executed.isFailure()) log.error(executed.getMessage());
                }

                if (event.getEventType().equalsIgnoreCase("users")) {
                    Result<Void> executed = retryUser.execute(event);

                    if (executed.isFailure()) log.error(executed.getMessage());
                }

                if (event.getEventType().equalsIgnoreCase("urls")) {
                    Result<Void> executed = retryUrl.execute(event);

                    if (executed.isFailure()) log.error(executed.getMessage());
                }

                if (event.getEventType().equalsIgnoreCase("urlAccessRule")) {
                    Result<Void> executed = retryUrlAccess.execute(event);

                    if (executed.isFailure()) log.error(executed.getMessage());
                }

                if (event.getEventType().equalsIgnoreCase("urlRedirectRule")) {
                    Result<Void> executed = retryUrlRedirect.execute(event);

                    if (executed.isFailure()) log.error(executed.getMessage());
                }

                if (event.getEventType().equalsIgnoreCase("urlTag")) {
                    Result<Void> executed = retryUrlTag.execute(event);

                    if (executed.isFailure()) log.error(executed.getMessage());
                }

            } catch (Exception ex) {
                log.error("Error retrying event {}", event.getId(), ex);
            }
        }
    }

}
