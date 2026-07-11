package com.notify.notify.modules.roles.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.roles.services.base.DeleteRoleByIdService;
import com.notify.notify.utils.annotations.UseService;
import com.notify.notify.utils.annotations.metrics.ObservedMetric;
import com.notify.notify.utils.annotations.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteRoleByIdServiceImpl implements DeleteRoleByIdService {
    RoleRepository repository;

    @Override
    @ResultTransaction
    @ObservedMetric("service.role.delete.id")
    public @NotNull Result<Void> execute(Long id) {
        int deleted = repository.deleteAndCount(id);

        if (deleted <= 0) {
            return Result.failure("Role not found", HttpStatus.NOT_FOUND);
        }

        return Result.success();
    }

}
