package com.notify.notify.modules.user.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.base.DeleteUserByIdService;
import com.notify.notify.utils.annotations.UseService;
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
public class DeleteUserByIdServiceImpl implements DeleteUserByIdService {
    UserRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> execute(Long id) {
        int count = repository.deleteAndCount(id);

        if (count == 0) {
            return Result.failure("User not found", HttpStatus.NOT_FOUND);
        }

        return Result.success();
    }

}
