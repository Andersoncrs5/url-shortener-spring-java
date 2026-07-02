package com.read.api.api.dto.role;

import com.read.api.api.dto.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "RoleDTO", description = "Detailed representation of a security role used for Role-Based Access Control (RBAC)")
public class RoleDTO extends BaseDTO {

    @Schema(description = "The unique canonical name code of the security authority role", example = "ROLE_ADMIN")
    String name;

    @Schema(description = "A comprehensive explanation detailing the permissions granted by this authority group", example = "Grants full administrative capabilities over link management and system configurations")
    String description;

    @Schema(description = "The active status flag indicating if this authority profile can currently be assigned to user accounts", example = "true")
    boolean active;
}