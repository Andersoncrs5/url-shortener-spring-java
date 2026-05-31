package com.write.api.application.service.userRole;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.ports.in.userRole.DeleteUserRoleUseCase;
import com.write.api.ports.out.repository.IRoleRepository;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUserRoleService implements DeleteUserRoleUseCase {

    IUserRepository userRepository;
    IRoleRepository roleRepository;
    IUserRoleRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> deleteById(
            Long id,
            Long performedByUserId
    ) {
        UserRoleModel link = repository.findById(id).orElse(null);
        if (link == null) {
            return Result.failure(
                    "User Role not found",
                    404
            );
        }

        UserModel targetUser = userRepository.findById(link.getUserId()).orElse(null);
        if (targetUser == null) {
            return Result.failure(
                    "User not found",
                    404
            );
        }

        RoleModel role = roleRepository.findById(link.getRoleId()).orElse(null);
        if (role == null) {
            return Result.failure(
                    "Role not found",
                    404
            );
        }

        var roles = repository.findRoleByUserId(performedByUserId);

        UserModel performedBy = userRepository.findById(performedByUserId).orElse(null);
        if (performedBy == null) {
            return Result.failure(
                    "Assigned user not found",
                    404
            );
        }

        boolean isSuperAdmin = roles != null && roles.contains("SUPER_ADMIN");

        if ("SUPER_ADMIN".equals(role.getName())) {
            return Result.failure(
                    "Cannot remove role SUPER_ADMIN",
                    409
            );
        }

        if ("ADMIN".equals(role.getName()) && !isSuperAdmin) {

            return Result.failure(
                    "Just one Super Adm can remove role admin",
                    409
            );
        }

        int deleted = repository.deleteById(id);

        if (deleted != 1) {
            return Result.failure(
                    "Failed to remove user role",
                    500
            );
        }

        return Result.success(200);
    }

}
