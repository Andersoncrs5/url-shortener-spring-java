package com.read.api.api.dto.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseDTO {
    Long id;
    Long version;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
