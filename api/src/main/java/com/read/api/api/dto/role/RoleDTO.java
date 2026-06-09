package com.read.api.api.dto.role;

import com.read.api.api.dto.base.BaseDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDTO extends BaseDTO {
    String name;
    String description;
    boolean active;
}
