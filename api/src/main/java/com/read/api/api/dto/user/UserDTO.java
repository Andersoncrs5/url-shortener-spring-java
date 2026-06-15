package com.read.api.api.dto.user;

import com.read.api.api.dto.base.BaseDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO extends BaseDTO {
    String name;
    String email;
    boolean active;
    ArrayList<String> roles = new ArrayList<>();
    LocalDateTime lastLoginAt;
    LocalDateTime blockedAt;
}
