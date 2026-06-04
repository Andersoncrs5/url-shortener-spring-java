package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserModel extends BaseModel {

    Long version;
    String name;
    String email;
    String refreshToken;
    String passwordHash;
    boolean active;
    boolean emailVerified;
    LocalDateTime lastLoginAt;
    LocalDateTime blockedAt;
    int attemptsLoginFailed = 0;

    public void sumAttemptLogin() {
        attemptsLoginFailed++;
    }

}