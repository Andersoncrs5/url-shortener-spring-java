package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.interfaces.user.InsertUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertUserUseCaseImpl implements InsertUserUseCase {
    UserRepository repository;

    @Override
    public Result<UserModel> execute(UserModel user) {
        UserModel saved = repository.insert(user);

        return Result.success(saved, 200);
    }

}
