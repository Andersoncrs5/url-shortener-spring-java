package com.read.api.domain.model;

import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.domain.model.base.BaseModel;
import com.read.api.domain.model.metrics.UrlMetricModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlModel extends BaseModel {

    Long userId;
    String shortCode;
    String description;
    String faviconUrl;
    String originalUrl;
    String title;
    String domain;
    UrlStatusEnum status;
    UrlAccessTypeEnum accessType;
    String passwordHash;
    @Getter(AccessLevel.NONE)
    Set<String> tags = new HashSet<>();

    @Getter
    @Setter(AccessLevel.NONE)
    final UrlMetricModel metric = new UrlMetricModel();

    boolean customAlias;
    LocalDateTime deletedAt;
    LocalDateTime expiresAt;
    LocalDateTime lastAccessAt;

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public boolean addTag(String tag) {

        if (tag == null || tag.isBlank()) {
            return false;
        }

        boolean added = tags.add(tag);

        if (added) {
            metric.incrementTagCount();
        }

        return added;
    }

    public boolean removeTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return false;
        }

        boolean removed = tags.remove(tag);

        if (removed) {
            metric.decrementTagCount();
        }

        return removed;
    }

}
