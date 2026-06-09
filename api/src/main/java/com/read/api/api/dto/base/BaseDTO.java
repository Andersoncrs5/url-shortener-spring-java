package com.read.api.api.dto.base;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseDTO {
    Long id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
