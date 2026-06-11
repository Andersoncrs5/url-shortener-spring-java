package com.read.api.application.usecase.impl.url;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.application.usecase.interfaces.url.FindAllUrlUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllUrlUseCaseImpl implements FindAllUrlUseCase {
    UrlRepository repository;

    public Page<UrlModel> execute(UrlFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }

}
