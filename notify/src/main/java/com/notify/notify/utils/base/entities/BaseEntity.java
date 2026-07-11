package com.notify.notify.utils.base.entities;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity extends Model {

    @Id
    protected Long id;

    @Version
    protected Long version;

    @WhenCreated
    protected LocalDateTime createdAt;

    @WhenModified
    protected LocalDateTime updatedAt;
}