package com.write.api.application.service.user;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.CreateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final IUserRepository repository;

    @Override
    public Result<UserModel> create(UserModel user) {
        return null;
    }

}
