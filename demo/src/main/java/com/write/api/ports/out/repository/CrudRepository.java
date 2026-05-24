package com.write.api.ports.out.repository;

import java.util.Optional;

public interface CrudRepository<T, ID> {

    T save(T entity);

    T insert(T entity);

    int deleteById(ID id);

    Optional<T> findById(ID id);

    boolean existsById(ID id);
}