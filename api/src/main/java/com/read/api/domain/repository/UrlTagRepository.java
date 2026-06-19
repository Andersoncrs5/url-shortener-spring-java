package com.read.api.domain.repository;

import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.base.BaseRepository;

public interface UrlTagRepository extends BaseRepository<UrlTagModel, Long, UrlTagFilter> {
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
