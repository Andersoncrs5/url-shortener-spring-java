package com.read.api.domain.repository;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.base.BaseRepository;

import java.util.Optional;

public interface UrlRepository extends BaseRepository<UrlModel, Long, UrlFilter> {
    Optional<UrlModel> findByShortCode(String code);
}
