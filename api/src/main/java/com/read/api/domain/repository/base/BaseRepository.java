package com.read.api.domain.repository.base;

import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.model.base.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseRepository<T extends BaseModel, ID, F extends BaseFilter> {
    T save(T model);
    T insert(T model);
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    int deleteById(ID id);
    Page<T> findAll(F filter, Pageable pageable);
}
