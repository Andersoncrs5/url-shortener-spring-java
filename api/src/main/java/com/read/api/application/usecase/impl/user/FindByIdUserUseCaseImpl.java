package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.interfaces.user.FindByIdUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindByIdUserUseCaseImpl implements FindByIdUserUseCase {
    UserRepository repository;

    @Override
    @Cacheable(value = "users", key = "#id")
    public Result<UserModel> execute(Long id) {
        var opt = repository.findById(id);

        return opt.map(Result::success)
                .orElseGet(() -> Result.failure("User not found", 404));
    }
}
