package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.user.ExistsEmailUserUseCase;
import com.read.api.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExistsEmailUserUseCaseImpl implements ExistsEmailUserUseCase {
    UserRepository repository;

    @Override
    public boolean execute(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }
}
