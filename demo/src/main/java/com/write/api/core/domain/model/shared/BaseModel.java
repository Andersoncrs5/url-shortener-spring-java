package com.write.api.core.domain.model.shared;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseModel {
    Long id;
    Long version;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}