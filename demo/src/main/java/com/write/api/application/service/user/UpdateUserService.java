package com.write.api.application.service.user;

import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.mapper.user.UserUpdateMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.UpdateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final IUserRepository repository;
    private final UserUpdateMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<UserModel> update(UserModel user, UpdateUserDTO dto) {
        mapper.updateUserFromDto(dto, user);

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPasswordHash(
                    passwordEncoder.encode(dto.password())
            );
        }

        UserModel updated = repository.save(user);

        return Result.success(updated, 200);
    }
}
