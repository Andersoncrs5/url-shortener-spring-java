package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.interfaces.user.DeleteByIdUserUseCase;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteByIdUserUseCaseImpl implements DeleteByIdUserUseCase {
    UserRepository repository;

    @Override
    @CacheEvict(value = "users", key = "#id")
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "User not found");
        }

        return Result.success();
    }
}
