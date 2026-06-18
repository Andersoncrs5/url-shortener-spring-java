package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.interfaces.urlTag.DeleteUrlTagByIdUseCase;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlTagByIdUseCaseImpl implements DeleteUrlTagByIdUseCase {
    UrlTagRepository repository;

    @Override
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted <= 0) {
            return Result.failure(404, "Url tag not found");
        }

        return Result.success();
    }

}
