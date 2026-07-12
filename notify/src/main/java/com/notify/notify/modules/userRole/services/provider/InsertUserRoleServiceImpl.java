package com.notify.notify.modules.userRole.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import com.notify.notify.modules.userRole.repository.UserRoleRepository;
import com.notify.notify.modules.userRole.services.base.InsertUserRoleService;
import com.notify.notify.utils.annotations.UseService;
import com.notify.notify.utils.annotations.metrics.ObservedMetric;
import com.notify.notify.utils.annotations.tx.ResultTransaction;
import com.notify.notify.utils.database.DatabaseConstraintHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertUserRoleServiceImpl implements InsertUserRoleService {

    UserRoleRepository repository;
    UserRepository userRepository;
    RoleRepository roleRepository;

    @Override
    @ResultTransaction
    @ObservedMetric("insert.user.role.service")
    public Result<UserRoleEntity> execute(UserRoleEntity userRole) {
        try {
            UserRoleEntity inserted = repository.insert(userRole);

            RoleEntity role = roleRepository.findById(userRole.getRoleId()).orElse(null);
            UserEntity user = userRepository.findById(userRole.getUserId()).orElse(null);

            if (user == null) return Result.failure("User not found", HttpStatus.NOT_FOUND);
            if (role == null) return Result.failure("Role not found", HttpStatus.NOT_FOUND);

            user.addRole(role.getName());

            userRepository.update(user);

            log.info("Role id={} mapped to User id={} successfully", userRole.getRoleId(), userRole.getUserId());

            return Result.success(inserted, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.toLowerCase().contains("uk_user_roles_user_role")) {
                return Result.failure(
                        "This role is already assigned to the user",
                        HttpStatus.CONFLICT
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}