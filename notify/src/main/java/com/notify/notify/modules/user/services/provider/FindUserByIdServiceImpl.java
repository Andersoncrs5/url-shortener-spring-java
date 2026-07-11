package com.notify.notify.modules.user.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.base.FindUserByIdService;
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
public class FindUserByIdServiceImpl implements FindUserByIdService {
    UserRepository repository;

    @Override
    @ResultTransaction(readOnly = true)
    public Result<UserEntity> execute(Long id) {
        UserEntity user = repository.findById(id).orElse(null);

        if (user == null) {
            return Result.failure("User not found", HttpStatus.NOT_FOUND);
        }

        return Result.success(user);
    }

}
