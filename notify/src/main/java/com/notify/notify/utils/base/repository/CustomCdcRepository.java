package com.notify.notify.utils.base.repository;

import com.notify.notify.utils.base.entities.BaseEntity;

public interface CustomCdcRepository<T extends BaseEntity> {
    T forceInsertWithId(T entity);
}