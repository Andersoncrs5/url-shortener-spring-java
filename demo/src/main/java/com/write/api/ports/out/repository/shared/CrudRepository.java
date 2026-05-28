package com.write.api.ports.out.repository.shared;

import com.write.api.shared.validation.snowflake.IsId;

import java.util.Optional;

public interface CrudRepository<T, ID> {

    T save(T entity);

    T insert(T entity);

    int deleteById(@IsId ID id);

    Optional<T> findById(@IsId ID id);

    boolean existsById(@IsId ID id);
}