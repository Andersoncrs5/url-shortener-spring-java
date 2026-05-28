package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleModel extends BaseModel {
    private Long userId;
    private Long roleId;
    private Long assignedByUserId;
}