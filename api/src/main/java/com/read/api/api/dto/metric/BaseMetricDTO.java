package com.read.api.api.dto.metric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseMetricDTO {
    long totalViews;
    long totalBlocked;
    long totalErrors;
}
