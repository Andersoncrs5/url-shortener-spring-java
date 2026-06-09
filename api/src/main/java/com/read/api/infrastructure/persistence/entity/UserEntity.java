package com.read.api.infrastructure.persistence.entity;

import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Document(collection = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {

    String name;

    @Indexed
    String email;

    boolean active;

    ArrayList<String> roles = new ArrayList<>();

    LocalDateTime lastLoginAt;
    LocalDateTime blockedAt;

}