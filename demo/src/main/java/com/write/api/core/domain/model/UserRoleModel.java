package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRoleModel extends BaseModel {
    Long userId;
    Long roleId;
    Long assignedByUserId;
}