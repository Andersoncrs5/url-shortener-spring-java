package com.read.api.application.usecase.interfaces.url;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.domain.model.UrlModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllUrlUseCase {
    Page<UrlModel> execute(UrlFilter filter, Pageable pageable);
}
