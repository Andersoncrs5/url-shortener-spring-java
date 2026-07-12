package com.notify.notify.modules.userRole.entities;

import com.notify.notify.utils.base.entities.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleEntity extends BaseEntity {

    private Long userId;
    private Long roleId;

}
