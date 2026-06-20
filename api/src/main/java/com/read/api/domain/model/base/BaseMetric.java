package com.read.api.domain.model.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
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
}