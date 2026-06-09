package com.read.api.infrastructure.persistence.entity.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseEntity {

    @Id
    Long id;
    Long version;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}