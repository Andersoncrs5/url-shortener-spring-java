package com.notify.notify.utils.base.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEntity {

    protected Long id;

    protected Long version = 0L;

    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;
}