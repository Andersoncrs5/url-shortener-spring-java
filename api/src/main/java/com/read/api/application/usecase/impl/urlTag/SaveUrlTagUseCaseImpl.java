package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.interfaces.urlTag.SaveUrlTagUseCase;
import com.read.api.domain.model.UrlTagModel;
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
public class SaveUrlTagUseCaseImpl implements SaveUrlTagUseCase {
    UrlTagRepository repository;

    @Override
    public Result<UrlTagModel> execute(UrlTagModel model) {
        UrlTagModel save = repository.save(model);

        return Result.success(save, 200);
    }
}
