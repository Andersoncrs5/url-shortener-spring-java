package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.domain.model.UrlTagModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindFilterUrlTagUseCase {
    Page<UrlTagModel> execute(UrlTagFilter filter, Pageable pageable);
}
