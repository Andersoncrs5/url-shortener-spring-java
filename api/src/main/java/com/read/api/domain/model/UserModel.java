package com.read.api.domain.model;

import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserModel extends BaseModel {

    String name;
    String email;
    boolean active;
    ArrayList<String> roles = new ArrayList<>();
    LocalDateTime lastLoginAt;
    LocalDateTime blockedAt;
}