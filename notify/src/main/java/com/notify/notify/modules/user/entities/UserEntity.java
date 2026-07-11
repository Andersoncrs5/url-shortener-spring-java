package com.notify.notify.modules.user.entities;

import com.notify.notify.utils.base.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_name", columnNames = "name"),
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_name", columnList = "name"),
        }
)
@Getter
@Setter
public class UserEntity extends BaseEntity implements Persistable<Long> {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private Boolean emailVerified;

    @Column(nullable = true)
    private @NonNull LocalDateTime blockedAt;

    private Set<String> roles;

    @Transient
    private boolean isNew = true;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}