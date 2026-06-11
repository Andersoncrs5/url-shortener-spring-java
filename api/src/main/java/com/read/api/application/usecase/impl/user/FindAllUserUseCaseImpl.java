package com.read.api.application.usecase.impl.user;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.application.usecase.interfaces.user.FindAllUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllUserUseCaseImpl implements FindAllUserUseCase {
    UserRepository repository;

    @Override
    public Page<UserModel> execute(UserFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }
}
