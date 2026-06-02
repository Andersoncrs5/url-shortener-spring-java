package com.write.api.infrastructure.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCrudServiceImpl implements RedisCrudService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void save(String key, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save ruleValue in Redis", e);
        }
    }

    @Override
    public <T> void save(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save ruleValue in Redis", e);
        }
    }

    @Override
    public <T> Optional<T> find(String key, Class<T> type) {
        try {
            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(json, type));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read ruleValue from Redis", e);
        }
    }

    @Override
    public <T> Optional<T> find(String key, TypeReference<T> typeReference) {
        try {
            String json = redisTemplate.opsForValue().get(key);

            if (json == null) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(json, typeReference));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read ruleValue from Redis", e);
        }
    }

    @Override
    public boolean exists(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public long delete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }

        Long deleted = redisTemplate.delete(keys);
        return deleted == null ? 0L : deleted;
    }

    @Override
    public boolean saveIfAbsent(String key, Object value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, json, ttl);
            return Boolean.TRUE.equals(ok);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save ruleValue in Redis", e);
        }
    }

    @Override
    public boolean expire(String key, Duration ttl) {
        Boolean ok = redisTemplate.expire(key, ttl);
        return Boolean.TRUE.equals(ok);
    }
}