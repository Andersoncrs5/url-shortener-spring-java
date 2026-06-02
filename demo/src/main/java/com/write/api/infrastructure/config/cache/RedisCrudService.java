package com.write.api.infrastructure.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

public interface RedisCrudService {

    <T> void save(String key, T value, Duration ttl);

    <T> void save(String key, T value);

    <T> Optional<T> find(String key, Class<T> type);

    <T> Optional<T> find(String key, TypeReference<T> typeReference);

    boolean exists(String key);

    void delete(String key);

    long delete(Collection<String> keys);

    boolean saveIfAbsent(String key, Object value, Duration ttl);

    boolean expire(String key, Duration ttl);
}