package com.write.api.infrastructure.scheduler;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.url.DeleteUrlByIdForceUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlDeleteJob {

    IUrlRepository repository;
    DeleteUrlByIdForceUseCase deleteUrlByIdForce;

    @Scheduled(
            fixedDelay = 10,
            timeUnit = TimeUnit.MINUTES
    )
    public void delete() {
        List<UrlModel> list = repository.findToDelete(
                UrlStatusEnum.DELETED,
                100,
                LocalDateTime.now().minusDays(7)
        );

        for (var url: list) {
            Result<Void> result = deleteUrlByIdForce.execute(url.getId());

            if (result.isFailure()) {
                log.error("Error the delete url, {}", result.getMessage());
            }

        }
    }

}
