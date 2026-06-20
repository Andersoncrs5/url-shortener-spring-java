package com.read.api.domain.model.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseMetric {

    long totalViews;
    long totalBlocked;
    long totalErrors;

    public void incrementViews() {
        totalViews++;
    }

    public void incrementBlocked() {
        totalBlocked++;
    }

    public void incrementErrors() {
        totalErrors++;
    }

    protected <K> void increment(
            Map<K, Long> map,
            K key
    ) {
        map.merge(key, 1L, Long::sum);
    }
}