package com.write.api.core.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UserModel {

    private Long id;
    private Long version;
    private String name;
    private String email;
    private String refreshToken;
    private String passwordHash;
    private boolean active;
    private boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime blockedAt;
    private int attemptsLoginFailed = 0;

    public void sumAttemptLogin() {
        attemptsLoginFailed++;
    }

}