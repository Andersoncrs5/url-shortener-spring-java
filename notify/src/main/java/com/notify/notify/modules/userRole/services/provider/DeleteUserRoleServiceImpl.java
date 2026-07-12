package com.notify.notify.modules.userRole.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import com.notify.notify.modules.userRole.repository.UserRoleRepository;
import com.notify.notify.modules.userRole.services.base.DeleteUserRoleService;
import com.notify.notify.utils.annotations.UseService;
import com.notify.notify.utils.annotations.metrics.ObservedMetric;
import com.notify.notify.utils.annotations.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUserRoleServiceImpl implements DeleteUserRoleService {

    UserRoleRepository repository;
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Override
    @ResultTransaction
    @ObservedMetric("delete.user.role.service")
    public Result<Void> execute(Long id) {
        try {
            UserRoleEntity userRole = repository.findById(id).orElse(null);
            if (userRole == null) {
                return Result.failure("User Role not found", HttpStatus.NOT_FOUND);
            }

            UserEntity user = userRepository.findById(userRole.getUserId()).orElse(null);
            if (user == null) {
                return Result.failure("User not found", HttpStatus.NOT_FOUND);
            }

            RoleEntity role = roleRepository.findById(userRole.getRoleId()).orElse(null);
            if (role == null) {
                return Result.failure("Role not found", HttpStatus.NOT_FOUND);
            }

            int count = repository.deleteAndCount(id);
            if (count <= 0) {
                return Result.failure("User Role could not be deleted or was already removed", HttpStatus.NOT_FOUND);
            }

            user.removeRole(role.getName());
            userRepository.update(user);

            log.info("Role id={} removed from User id={} successfully", userRole.getRoleId(), userRole.getUserId());

            return Result.success(null, HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            log.error("Error deleting user role mapping: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}