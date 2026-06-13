package com.write.api.application.service.url;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.url.DeleteUrlByIdSoftUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlByIdSoftService implements DeleteUrlByIdSoftUseCase {

    IUrlRepository repository;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.delete.soft")
    public Result<Void> execute(Long id) {
        UrlModel url = repository.findById(id).orElse(null);

        if (url == null) {
            return Result.failure(404, "Url not found");
        }

        url.setStatus(UrlStatusEnum.DELETED);
        url.setDeletedAt(LocalDateTime.now());

        repository.save(url);

        return Result.success();
    }

}
