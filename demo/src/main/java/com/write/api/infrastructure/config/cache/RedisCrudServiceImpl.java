package com.write.api.infrastructure.config.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisCrudServiceImpl implements RedisCrudService {

    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper;

    @Override
    @TrackExecutionTime("redis.save.time")
    public <T> void save(String key, T value, Duration ttl) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save ruleValue in Redis", e);
        }
    }

    @Override
    @TrackExecutionTime("redis.save")
    public <T> void save(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save ruleValue in Redis", e);
        }
    }

    @Override
    @TrackExecutionTime("redis.find")
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
    @TrackExecutionTime("redis.find.type.reference")
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
    @TrackExecutionTime("redis.exists")
    public boolean exists(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(result);
    }

    @Override
    @TrackExecutionTime("redis.delete")
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    @TrackExecutionTime("redis.delete.any")
    public long delete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return 0L;
        }

        Long deleted = redisTemplate.delete(keys);
        return deleted == null ? 0L : deleted;
    }

    @Override
    @TrackExecutionTime("redis.save.absent")
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
    @TrackExecutionTime("redis.expire")
    public boolean expire(String key, Duration ttl) {
        Boolean ok = redisTemplate.expire(key, ttl);
        return Boolean.TRUE.equals(ok);
    }
}