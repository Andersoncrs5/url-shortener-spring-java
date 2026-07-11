package com.notify.notify.modules.user.entities;

import com.notify.notify.utils.base.entities.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserEntity extends BaseEntity {

    private String name;

    private String email;

    private Boolean active;

    private Boolean emailVerified;

    private LocalDateTime blockedAt;

    private Set<String> roles;

    public UserEntity() {
        this.blockedAt = null;
        this.roles = new HashSet<>();
    }

}