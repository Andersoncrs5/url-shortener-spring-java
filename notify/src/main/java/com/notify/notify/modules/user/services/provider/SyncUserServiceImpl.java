package com.notify.notify.modules.user.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.base.SyncUserService;
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
public class SyncUserServiceImpl implements SyncUserService {
    UserRepository repository;

    @Override
    @ResultTransaction
    @ObservedMetric("sync.user.service")
    public Result<UserEntity> execute(UserEntity user) {
        try {
            repository.update(user);


            log.info("Saving user id={}, version={}", user.getId(), user.getVersion());

            return Result.success(user, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.toLowerCase().contains("uk_users_email")) {
                return  Result.failure(
                        "Email already exists",
                        HttpStatus.FORBIDDEN
                );
            }

            if (message.toLowerCase().contains("uk_users_name")) {
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
