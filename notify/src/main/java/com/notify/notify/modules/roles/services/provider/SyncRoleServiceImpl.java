package com.notify.notify.modules.roles.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.roles.services.base.SyncRoleService;
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
public class SyncRoleServiceImpl implements SyncRoleService {
    RoleRepository repository;

    @Override
    @ResultTransaction
    @ObservedMetric("sync.role.service")
    public Result<RoleEntity> execute(RoleEntity role) {
        try {
            repository.update(role);

            return Result.success(role);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.toLowerCase().contains("uk_roles_name")) {
                return  Result.failure(
                        "Name already exists",
                        HttpStatus.FORBIDDEN
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
