package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.interfaces.user.ExistsUserByNameUseCase;
import com.read.api.domain.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExistsUserByNameUseCaseImpl implements ExistsUserByNameUseCase {
    UserRepository repository;

    @Override
    public boolean execute(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}
